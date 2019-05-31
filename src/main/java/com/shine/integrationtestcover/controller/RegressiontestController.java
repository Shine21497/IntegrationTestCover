package com.shine.integrationtestcover.controller;

import com.shine.integrationtestcover.config.BaseConfig;
import com.shine.integrationtestcover.service.RegressionTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
public class RegressiontestController {
    @Autowired
    BaseConfig baseConfig;
    @Autowired
    RegressionTestService regressiontestService;
    @RequestMapping(value="/regressiontest")
    @ResponseBody
    public List<String> regressiontest(@RequestParam("oldJarName") String oldJarName, @RequestParam("newJarName") String newJarName, @RequestParam("packageName") String packageName) throws Exception {
    //判断jar文件是否存在
        List<String> result= new ArrayList<String>();
        File oldJar=new File(baseConfig.getUploadedFilePath()+"//"+oldJarName);
        File newJar=new File(baseConfig.getRegressionFilePath()+"//"+newJarName);
        if (!oldJar.exists()) {
            result.add("Jar file " + oldJarName + " does not exist");
            return result;
        }
        if(!newJar.exists()){
            result.add("Jar file " + newJarName + " does not exist");
            return result;
        }
        result=regressiontestService.regressiontest(oldJarName,newJarName,packageName);
        System.out.println("result show:");
        for(int i=0;i<result.size();i++){
            System.out.println(result.get(i));
        }
        return result;
    }
}
