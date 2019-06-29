package com.shine.integrationtestcover.controller;

import com.shine.integrationtestcover.config.BaseConfig;
import com.shine.integrationtestcover.service.RegressionTestService;
import com.shine.integrationtestcover.service.RunTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class RegressiontestController {
    @Autowired
    BaseConfig baseConfig;
    @Autowired
    RunTestService runTestService;
    @Autowired
    RegressionTestService regressiontestService;
    @RequestMapping(value = "/versions")
    @ResponseBody
    public List<String> versions(@RequestParam("project") String project) {
        List<String> fileList = new ArrayList<String>();
        File projectFile = new File(baseConfig.getRegressionFilePath() + project);
//如果文件夹不存在则创建
        if (!projectFile.exists() && !projectFile.isDirectory()) {
            System.out.println("//不存在");
            projectFile.mkdir();
        } else {
            File[] fs = projectFile.listFiles();    //遍历path下的文件和目录，放在File数组中
            for (File f : fs) {                    //遍历File[]数组
                if (!f.isDirectory())        //若非目录(即文件)，则打印
                    fileList.add(f.getName());
            }
        }
        return fileList;
    }
    @RequestMapping(value="/regressiontest")
    @ResponseBody
    public List<String> regressiontest(@RequestParam("oldJarName") String oldJarName, @RequestParam("newJarName") String newJarName, @RequestParam("packageName") String packageName) throws Exception {
    //判断jar文件是否存在
        String project = oldJarName.substring(0, oldJarName.indexOf("."));
        List<String> result= new ArrayList<String>();
        File oldJar=new File(baseConfig.getUploadedFilePath()+oldJarName);
        File newJar=new File(baseConfig.getRegressionFilePath()+"//"+project+"//"+newJarName);
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
