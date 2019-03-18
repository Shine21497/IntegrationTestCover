package com.shine.integrationtestcover.controller;


import com.shine.integrationtestcover.config.BaseConfig;
import com.shine.integrationtestcover.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@RestController
public class FileParserController {

    @Autowired
    GraphService graphService;

    @Autowired
    BaseConfig baseConfig;

    @RequestMapping(value = "/relation",method = RequestMethod.GET)
    public HashMap<String, Object> getInvokeRelationship(@RequestParam String name){
        graphService.setFilename(name);
        graphService.setPath(baseConfig.getUploadedFilePath());
        graphService.initiate();
        ArrayList edges=graphService.getEdges();
        ArrayList<HashMap<String, Object>> vertex=graphService.getVertex();
        HashMap<String, Object> result = new HashMap<>();
        result.put("nodes", vertex);
        result.put("links", edges);
        return result;

    }
}
