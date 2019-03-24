package com.shine.integrationtestcover.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: WHQ
 * @Date: 2019/3/23 19:38
 */
public class Runtest {
    @Autowired
    RunTestService runTestService=new RunTestService();

    @Before
    public void initiate(){
        runTestService.setJarname("com.dynamic-1.0-SNAPSHOT.jar");//项目jar包
        runTestService.setJarpath("C://Users//22831//Desktop//lib");
        runTestService.setTestwaypath("C://Users//22831//Desktop//lib");//JUnit测试包位置,可以让用户选择
        runTestService.setTestwayname("junit-4.10.jar");

        runTestService.setJavafilepath("C://Users//22831//Desktop");//.java测试用例的位置
        runTestService.setJavafilename("test1");
    }

    @Test
    public void HandleJavaTest(){
        runTestService.handleJavaFile();
    }


    @Test
    public void CompileJava(){
        runTestService.compileJava();
    }

    @Test
    public void InvokeMethod(){
        runTestService.invokeMethod();
    }
}
