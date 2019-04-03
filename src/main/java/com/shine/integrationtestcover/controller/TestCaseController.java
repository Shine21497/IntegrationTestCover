package com.shine.integrationtestcover.controller;

import com.shine.integrationtestcover.service.RunTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: WHQ
 * @Date: 2019/3/23 19:38
 */
@RestController
public class TestCaseController {

    @Autowired
    RunTestService runTestService;

    private static int finishtestcase=0;

    private final static int sumtestcase=0;//测试用例的总数

    //获得一个java测试文件的所有测试用例的名称
    @RequestMapping(value = "/getmethods", method = RequestMethod.GET)
    public List<String> getMethods(String projectname, String testcasename) {
        List<String> methods = new LinkedList<>();
        runTestService.initate(projectname, testcasename);
        methods = runTestService.getMethods(testcasename);
        return methods;
    }


    @RequestMapping(value = "/runtestcase", method = RequestMethod.GET)
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

    /*
    接收项目名，返回已经完成数和总数 n/m,执行所有测试用例
     */
    @RequestMapping(value = "/finish", method = RequestMethod.GET)
    @Async
    public List getFinish(String projectname) {
        runTestService.runall();
        System.out.println(Thread.currentThread().getName());
        List<String> finish=new LinkedList<>();

        finish.add(String.valueOf(runTestService.getTask()));
        finish.add(String.valueOf(sumtestcase));

        return finish;
    }

    @RequestMapping(value = "/finishedtask", method = RequestMethod.GET)
    @Async
    public List getFinishedTest(String projectname) {
        runTestService.runall();
        System.out.println(Thread.currentThread().getName());

        List<String> finish=runTestService.getAllmethods();
        return finish;
    }

}

