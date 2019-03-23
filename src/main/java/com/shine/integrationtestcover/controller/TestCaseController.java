package com.shine.integrationtestcover.controller;

import com.shine.integrationtestcover.service.RunTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Author: WHQ
 * @Date: 2019/3/23 19:38
 */
public class TestCaseController {
    @Autowired
    RunTestService runTestService;

    @RequestMapping(value = "/testcase",method = RequestMethod.GET)
    public String runTestCase(String filename) {
        runTestService.setJarname("com.dynamic-1.0-SNAPSHOT.jar");//项目jar包
        runTestService.setJarpath("C://Users//22831//Desktop//lib");
        runTestService.setTestwaypath("C://Users//22831//Desktop//lib");//JUnit测试包位置,可以让用户选择
        runTestService.setTestwayname("junit-4.10.jar");

        runTestService.setJavafilepath("C://Users//22831//Desktop");//.java测试用例的位置
        runTestService.setJavafilename(filename);

        runTestService.handleJavaFile();//去掉第一行的包声明

        runTestService.compileJava();//编译

        runTestService.invokeMethod();//跑测试用例

        return "success";
    }

    }
