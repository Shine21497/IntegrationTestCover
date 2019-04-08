package com.shine.integrationtestcover.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: WHQ
 * @Date: 2019/4/8 12:14
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAsync
public class TestCaseControllerTest {
    @Autowired
    TestCaseController testCaseController;

    @Test
    public void testasync(){

    }
}
