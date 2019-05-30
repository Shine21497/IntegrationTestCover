package com.shine.integrationtestcover.controller;

import com.shine.integrationtestcover.service.RunTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @Author: WHQ
 * @Date: 2019/3/23 19:38
 */
@RestController
public class TestCaseController {

    @Autowired
    RunTestService runTestService;

    private String projectname;

    private String testcasename;

    private String methodname;


    private String key;

    //获得一个java测试文件的所有测试用例的名称
    @RequestMapping(value = "/getMethods", method = RequestMethod.GET)
    public List<String> getMethods(String projectname, String testcasename) {
        runTestService.initate(projectname, true);
        runTestService.compileJava(testcasename);
        List<String> methods = new LinkedList<>();

        methods = runTestService.getMethods(testcasename);
        return methods;
    }

    @RequestMapping(value = "/runTestCase", method = RequestMethod.GET)
    public List runTestCase(String projectname, String testcasename, String method)throws Exception {
        testcasename = testcasename.replace(".java", "");
        projectname = projectname.replace(".jar", "");
        runTestService.initate(projectname, true);
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("MMddHHmmss");
        Date date = new Date();
        String par = sdf.format(date);//获取当前时间
        Random random = new Random();
        int a = random.nextInt(100);
        String key = par + String.valueOf(a);//生成key
        this.key = key;
        String type="";
        List result=new LinkedList();
        System.out.println("获取key:"+Thread.currentThread().getName());
        if (method.equals("allMethods") == false) {
            type="one";
            runTestService.invokeMethod(testcasename, method);
        }
        else if (method.equals("allMethods") && testcasename.equals("allTestFiles") == false)//指定跑某个java文件里面的所有测试用例
        {
            type="many";
            runTestService.runTest(testcasename);
        }
        else if (testcasename.equals("allTestFiles") && method.equals("allMethods"))//跑此项目下的所有
        {
            type="many";
            runTestService.runAll();
        }

        result.add(key);
        result.add(type);
        return result;
    }

  /*  @Async
    public void run(String projectname, String testcasename, String method) throws Exception{

        System.out.println("运行:"+Thread.currentThread().getName());
        if (method.equals("all") == false) {
            runTestService.invokeMethod(testcasename, method);
        }
        else if (method.equals("all") && testcasename.equals("all") == false)//指定跑某个java文件里面的所有测试用例
        {
            runTestService.runTest(testcasename);
        }
        else if (testcasename.equals("all") && method.equals("all"))//跑此项目下的所有
        {
            runTestService.runAll();
        }

    }
*/

    @RequestMapping(value = "/getInvokingProcess", method = RequestMethod.GET)
    public List getInvokingProcess(String key) {
        List result = new LinkedList();
        if (!key.equals(this.key)) {
            result.add("sorry,no this task~");
            return result;
        } else {
            result = runTestService.getRunprocess();
            return result;
        }

    }

    @RequestMapping(value = "/getInvokingResults", method = RequestMethod.GET)
    public List getInvokingResults(String key) {
        List result = new LinkedList();
        if (key.equals(this.key)) {
            result = runTestService.getRunresults();
            return result;
        } else {
            result.add("sorry,no this task~");
            return result;
        }

    }


}

