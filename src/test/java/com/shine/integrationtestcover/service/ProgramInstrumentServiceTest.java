package com.shine.integrationtestcover.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: Shine
 * @Date: 2019/4/15
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProgramInstrumentServiceTest {
    @Autowired
    ProgramInstrumentService programInstrumentService;

    @Test
    public void test(){
        try {
            programInstrumentService.doInstrumentation("demo.jar");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
