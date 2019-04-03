package com.shine.integrationtestcover.service;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.List;

/**
 * @Author: WHQ
 * @Date: 2019/3/23 19:38
 */
@EnableAsync
public class Runtest {
    @Autowired
    RunTestService runTestService=new RunTestService();

    @Before
    public void initiate(){
        runTestService.setJarname("demo.jar");//项目jar包
        runTestService.setJarpath("C://Users//22831//Desktop//lib");
        runTestService.setTestwaypath("C://Users//22831//Desktop//lib");//JUnit测试包位置,可以让用户选择
        runTestService.setTestwayname("junit-4.10.jar");

        runTestService.setJavafilepath("C://Users//22831//Desktop");//.java测试用例的位置
       // runTestService.setJavafilename("TestMethod");
    }

    @Test
    public void HandleJavaTest(){
        //runTestService.handleJavaFile();
    }

    @Test
    public void GetPackageName(){
        String Javafilename="TestMethod";
        //System.out.println(runTestService.getPackagename(Javafilename));
        runTestService.handlePackageName(Javafilename);
    }

    @Test
    public void CompileJava(){
        String Javafilename="TestMethod";
        runTestService.compileJava(Javafilename);
    }

    @Test
    public void getMethods(){
        runTestService.getMethods("TestMethod");
    }
    @Test
    public void InvokeMethod(){
        String Javafilename="TestMethod";
        List list=runTestService.invokeMethod(Javafilename,"te11");
        for(int i=0;i<list.size();i++){
            System.out.println(list.get(i));
        }
    }

    @Test
    public void rull(){
        final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
        logger.info("=============" + Thread.currentThread().getName() + "异步");
        runTestService.runall();
    }
}
