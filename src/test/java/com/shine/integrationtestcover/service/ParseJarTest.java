package com.shine.integrationtestcover.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ParseJarTest {

    @Autowired
    ParseJarService parseJarService;

    @Test
    public void test(){
        parseJarService.setFilename("test.jar");
        parseJarService.setPath("C://Users//22831//Desktop//test//target");
        System.out.println(parseJarService.getInvoking());
    }

}
