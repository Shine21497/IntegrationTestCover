package com.shine.integrationtestcover.config;

import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @Author: Shine
 * @Date: 2019/3/18
 */
@Component
public class BaseConfig {
    private String uploadedFilePath = "";

    public String getUploadedFilePath() {
        if(this.uploadedFilePath.isEmpty()) {
            this.uploadedFilePath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile()+ "uploadedJar/";
            try{
                this.uploadedFilePath = java.net.URLDecoder.decode(this.uploadedFilePath, "UTF-8");
            }catch(Exception e)
            {
                e.printStackTrace();
            };
            File directory=new File(this.uploadedFilePath);
            if(!directory.exists())
            {
                try{
                    directory.mkdirs();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return this.uploadedFilePath;
    }

    public String getUploadedTestPath(String projectName) {
        projectName.replace(".jar","");
        String uploadedTestPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile()+ "uploadedTestCase/" + projectName ;
        try{
            uploadedTestPath = java.net.URLDecoder.decode(uploadedTestPath, "UTF-8");
        }catch(Exception e)
        {
            e.printStackTrace();
        };
        File directory=new File(uploadedTestPath);
        if(!directory.exists())
        {
            try{
                directory.mkdirs();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return uploadedTestPath;
    }
}
