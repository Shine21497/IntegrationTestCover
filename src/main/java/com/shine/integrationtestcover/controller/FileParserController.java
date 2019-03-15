package com.shine.integrationtestcover.controller;


import com.shine.integrationtestcover.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

@RestController
public class FileParserController {

    @Autowired
    GraphService graphService;

    @RequestMapping(value = "/filename",method = RequestMethod.GET)
    public ArrayList getInvokeRelationship(@RequestParam String name){
        graphService.setFilename(name);
        graphService.setPath("C://Users//22831//Desktop//test//target");
        graphService.initiate();
        ArrayList edges=graphService.getEdges();
        ArrayList<String> vertex=graphService.getVertexResult();
        edges.add(vertex);
        return edges;

    }
}
