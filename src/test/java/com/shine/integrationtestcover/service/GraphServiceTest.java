package com.shine.integrationtestcover.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GraphServiceTest {

    @Autowired
    GraphService graphService;


    @Test
    public void getVertex(){
        graphService.setFilename("test.jar");
        graphService.setPath("C://Users//22831//Desktop//test//target");
        graphService.getEdges();
      //  graphService.getVertexResult();
    }
}
