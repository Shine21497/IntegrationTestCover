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
    @RequestMapping(value = "/getmethods", method = RequestMethod.GET)
    public List<String> getMethods(String projectname, String testcasename) {
        List<String> methods = new LinkedList<>();
        runTestService.initate(projectname);
        methods = runTestService.getMethods(testcasename);
        return methods;
    }

    @RequestMapping(value = "/runTestCase", method = RequestMethod.GET)
    public List runTestCase(String projectname, String testcasename, String method)throws Exception {
        runTestService.initate(projectname);
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
        if (method.equals("all") == false) {
            runTestService.invokeMethod(testcasename, method);
            type="one";
        }
        else if (method.equals("all") && testcasename.equals("all") == false)//指定跑某个java文件里面的所有测试用例
        {
            runTestService.runTest(testcasename);
            type="many";
        }
        else if (testcasename.equals("all") && method.equals("all"))//跑此项目下的所有
        {
            runTestService.runAll();
            type="many";
        }
        result.add(key);
        result.add(type);
        return result;
    }


    @RequestMapping(value = "/getInvokingProcess", method = RequestMethod.GET)
    public List getInvokingProcess(String key) {
        List result = new LinkedList();
        System.out.println(this.key + "key");
        if (key != this.key) {
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
            System.out.println("zibi");
            result = runTestService.getRunresults();
            return result;
        } else {
            System.out.println("kule");
            result.add("sorry,no this task~");
            return result;
        }

    }


}

 /*  @RequestMapping(value = "/runtestcase", method = RequestMethod.GET)
    public List<String> runTestCase(String projectname, String testcasename) {

        List<String> invoking = new LinkedList<>();

        runTestService.initate(projectname, testcasename);//初始化，包括建文件夹以及编译java文件

        String packagename = runTestService.getPackagename(testcasename);//获得包名 "."分隔

        List<String> methods = runTestService.getMethods(testcasename);//获得所有方法名
        for (int i = 0; i < methods.size(); i++) {
            List<String> onetestcaseinvoking = runTestService.invokeMethod(testcasename,methods.get(i));
         //   finishtestcase++;
            invoking.addAll(onetestcaseinvoking);
        }
        return invoking;
    }

    *//*
    接收项目名，返回已经完成数和总数 n/m,执行所有测试用例
     *//*
    @RequestMapping(value = "/finish", method = RequestMethod.GET)
    @Async
    public List getFinish(String projectname,String javafilename) {
        runTestService.runall(javafilename);
        System.out.println(Thread.currentThread().getName());
        List<String> finish=new LinkedList<>();

        finish.add(String.valueOf(runTestService.getTask()));
        finish.add(String.valueOf(sumtestcase));

        return finish;
    }

    @RequestMapping(value = "/finishedtask", method = RequestMethod.GET)
    @Async
    public List getFinishedTest(String projectname,String javafilename) {
        runTestService.runall(javafilename);
        System.out.println(Thread.currentThread().getName());
        List<String> finish=runTestService.getAllmethods();
        return finish;
    }
    */