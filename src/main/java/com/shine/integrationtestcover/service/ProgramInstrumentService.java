package com.shine.integrationtestcover.service;

import com.shine.integrationtestcover.config.BaseConfig;
import com.shine.integrationtestcover.service.programInstrument.JarFileInput;
import com.shine.integrationtestcover.service.programInstrument.ProgramInstrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * @Author: Shine
 * @Date: 2019/4/14
 */
@Service
public class ProgramInstrumentService {

    public static boolean isComplete = false;
    public static HashMap<String,Integer> situation = new HashMap<>();

    @Autowired
    BaseConfig baseConfig;

    @Async
    public void doInstrumentation(String fileName) throws IOException {
        System.out.println("bbb"+ProgramInstrumentService.situation.get(fileName));
        if(!situation.keySet().contains(fileName)){
            situation.put(fileName,0);
        }
        //0未开始，1进行中，2已完成
        File file = new File(baseConfig.getInstrumentationPath() + fileName);
        if(file.exists()) {
            //默认项目不同版本需要在文件名上体现，即同一文件名就是同一个版本
            System.out.println("插桩已经完成过，不再进行");
            situation.put(fileName,2);
            return;
        }
        System.out.println("qw"+ProgramInstrumentService.situation.get(fileName));

        if(situation.get(fileName)==0) {
            System.out.println("进行中");
            situation.put(fileName,1);//开始插桩
            System.out.println("qw"+ProgramInstrumentService.situation.get(fileName));

            //fileName 是类似 “demo.jar”的文件名
            String getUploadedFilePath = baseConfig.getUploadedFilePath().substring(1).replace('/', '\\');
            String getRunTestProjectPath = baseConfig.getInstrumentationPath().substring(1).replace('/', '\\');
            //取得包名，建立文件夹
            int index = fileName.lastIndexOf('.');
            String sub = fileName.substring(0, index);
            mkDirectory(getUploadedFilePath + sub);
            //复制jar包到同名文件夹下，并且解压jar包
            File dir = new File(getUploadedFilePath + "\\" + sub);
            System.out.println("dir" + dir);
            String command = "cmd /c " + "copy  " + "\"" + getUploadedFilePath + fileName + "\"  \"" + getUploadedFilePath + sub + "\"";
            System.out.println(command);
            doCmd(command, dir);
            command = "jar -xvf " + fileName;
            doCmd(command, dir);
            //执行插桩函数
            JarFileInput.jarFileInput(getUploadedFilePath + sub, fileName);
            //删除同名文件夹下的jar包
            command = "cmd /c " + "del  \"" + getUploadedFilePath + sub + "\\" + fileName + "\"";
            System.out.println(command);
            doCmd(command, dir);
            //打包同名文件夹
            command = "cmd /c " + "jar cvfm " + fileName + " META-INF\\MANIFEST.MF ./";
            System.out.println(command);
            doCmd(command, dir);
            //将同名文件夹下的jar包复制到指定位置
            command = "cmd /c " + "copy  " + "\"" + getUploadedFilePath + sub + "\\" + fileName + "\"  \"" + getRunTestProjectPath + "\"";
            System.out.println(command);
            doCmd(command, dir);
            //删除创建的文件夹
            command = "cmd /c " + "rmdir /s/q   " + "\"" + getUploadedFilePath + sub + "\"";
            System.out.println(command);
            doCmd(command, dir);
            ProgramInstrumentService.isComplete = true;
            situation.put(fileName,2);//插桩结束
            System.out.println("已完成");
        }else if(situation.get(fileName)==1) {
            System.out.println("插桩冲突!");
        }

    }

    public static void doCmd(String command,File dir) throws IOException {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        try{
        Process p = Runtime.getRuntime().exec(command,null,dir);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "GBK"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            BufferedReader br2 = new BufferedReader(new InputStreamReader(p.getErrorStream(), "GBK"));
            String line2 = null;
            while ((line2 = br2.readLine()) != null) {
                sb2.append(line2 + "\n");
            }
            br.close();
            br2.close();
            p.waitFor();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
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
