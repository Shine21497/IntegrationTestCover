package com.shine.integrationtestcover.controller;

import com.shine.integrationtestcover.config.BaseConfig;
import com.shine.integrationtestcover.service.RegressiontestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

@RestController
public class RegressiontestController {
    @Autowired
    BaseConfig baseConfig;
    @Autowired
    RegressiontestService regressiontestService;
    @RequestMapping(value="/regressiontest")
    @ResponseBody
    public String regressiontest(@RequestParam("oldJarName") String oldJarName, @RequestParam("newJarName") String newJarName, @RequestParam("packageName") String packageName) throws Exception {
    //判断jar文件是否存在
        File oldJar=new File(baseConfig.getRegressionFilePath()+"//"+oldJarName);
        File newJar=new File(baseConfig.getRegressionFilePath()+"//"+newJarName);
        if (!oldJar.exists()) {
            return("Jar file " + oldJarName + " does not exist");
        }
        if(!newJar.exists()){
            return("Jar file " + newJarName + " does not exist");
        }
        regressiontestService.regressiontest(oldJarName,newJarName,packageName);
        return "hhh";

    }


}
