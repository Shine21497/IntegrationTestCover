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
        String getUploadedFilePath=baseConfig.getUploadedFilePath().substring(1).replace('/','\\');
        String getRunTestProjectPath=baseConfig.getRunTestProjectPath(fileName.replace("jar", "")).substring(1).replace('/','\\');
        //取得包名，建立文件夹
        int index=fileName.lastIndexOf('.');
        String sub=fileName.substring(0,index);
        mkDirectory(getUploadedFilePath+sub);
        //复制jar包到同名文件夹下，并且解压jar包
        File dir=new File(getUploadedFilePath+"\\"+sub);
        System.out.println("dir"+dir);
        String command="cmd /c "+"copy  "+"\""+ getUploadedFilePath+fileName+"\"  \""+ getUploadedFilePath+sub+"\"";
        System.out.println(command);
        doCmd(command,dir);
        command="jar -xvf "+fileName;
        doCmd(command,dir);
        //执行插桩函数
        JarFileInput.jarFileInput(getUploadedFilePath+sub,fileName);
        //删除同名文件夹下的jar包
        command="cmd /c "+"del  \""+getUploadedFilePath+sub+"\\"+fileName+"\"";
        System.out.println(command);
        doCmd(command,dir);
        //打包同名文件夹
        command="cmd /c "+"jar cvfm "+fileName+" META-INF\\MANIFEST.MF ./";
        System.out.println(command);
        doCmd(command,dir);
        //将同名文件夹下的jar包复制到指定位置
        command="cmd /c "+"copy  "+"\""+ getUploadedFilePath+sub+"\\"+fileName+"\"  \""+ getRunTestProjectPath+"\"";
        System.out.println(command);
        doCmd(command,dir);
        //删除创建的文件夹
        command="cmd /c "+"rmdir /s/q   "+"\""+ getUploadedFilePath+sub+"\"";
        System.out.println(command);
        doCmd(command,dir);

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
