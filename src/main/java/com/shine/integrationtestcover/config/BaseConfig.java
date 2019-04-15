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
    private String instrumentationPath = "";

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

    public String getInstrumentationPath() {
        if(this.instrumentationPath.isEmpty()) {
            this.instrumentationPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile()+ "instrumentation/";
            try{
                this.instrumentationPath = java.net.URLDecoder.decode(this.instrumentationPath, "UTF-8");
            }catch(Exception e)
            {
                e.printStackTrace();
            };
            File directory=new File(this.instrumentationPath);
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
        return this.instrumentationPath;
    }

    public String getUploadedTestPath(String projectName) {
        String newname=projectName.replace(".jar","");
        String uploadedTestPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile()+ "uploadedTestCase/" + newname + "/" ;
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

    public String getRunTestProjectPath(String projectName) {
        String newname = projectName.replace(".jar","");
        String runTestPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile()+ "runTestCase/" + newname + "/" ;
        try{
            runTestPath = java.net.URLDecoder.decode(runTestPath, "UTF-8");
        }catch(Exception e)
        {
            e.printStackTrace();
        };
        File directory=new File(runTestPath);
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
        return runTestPath;
    }
}
