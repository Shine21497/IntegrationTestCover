package com.shine.integrationtestcover.service;

import com.shine.integrationtestcover.config.BaseConfig;
import com.shine.integrationtestcover.service.programInstrument.JarFileInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Author: Shine
 * @Date: 2019/4/14
 */
@Service
public class ProgramInstrumentService {

    @Autowired
    BaseConfig baseConfig;

    public void doInstrumentation(String fileName) throws IOException {
        //fileName 是类似 “demo.jar”的文件名
        baseConfig.getUploadedFilePath();//这个是待插桩Jar文件的目录 最后带有斜杠 类似“xx/xx/xx/”
        baseConfig.getRunTestProjectPath(fileName.replace("jar", "")); //这个是插桩后结果所在的目录，插桩结束后jar包在这个路径

    }


    public static  boolean mkDirectory(String path){
        File file = null;
        try {
            file = new File(path);
            if (!file.exists()) {
                return file.mkdirs();
            }
            else{
                return false;
            }
        } catch (Exception e) {
        } finally {
            file = null;
        }
        return false;
    }
}
