package com.shine.integrationtestcover.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RegressionTestServiceTest {
    @Autowired
    RegressiontestService regressiontestService;
    @Test
    public void test(){
        try{
            List<String>result= regressiontestService.regressiontest("bean-query.jar","bean-query-after-change.jar","cn/jimmyshi");
            for(int i=0;i<result.size();i++){
                System.out.println(result.get(i));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
