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
        baseConfig.getInstrumentationPath(); //这个是插桩后结果所在的目录，插桩结束后jar包在这个路径
        //取得包名，建立文件夹
        int index=fileName.lastIndexOf('.');
        String sub=fileName.substring(0,index);
        mkDirectory(baseConfig.getUploadedFilePath()+sub);
        //复制jar包到同名文件夹下，并且解压jar包
        File dir=new File(baseConfig.getUploadedFilePath()+"\\"+sub);
        try{
            String command="cmd /c "+"copy  "+"\""+ baseConfig.getUploadedFilePath()+fileName+"\"  \""+ baseConfig.getUploadedFilePath()+sub+"\"";
            String command1="jar -xvf "+fileName;
            Process p = Runtime.getRuntime().exec(command);
            Process p2=Runtime.getRuntime().exec(command1,null,dir);
            p.waitFor();
            p2.waitFor();

        }catch (InterruptedException e){
            e.printStackTrace();
        }
        //执行插桩函数
        JarFileInput.jarFileInput(baseConfig.getUploadedFilePath()+sub,fileName);
        //删除同名文件夹下的jar包
        try {
            String command="cmd /c "+"del  \""+baseConfig.getUploadedFilePath()+sub+"\\"+fileName+"\"";
            System.out.println(command);
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        //打包同名文件夹
        try{
            String command="cmd /c "+"jar cvfm "+fileName+" META-INF\\MANIFEST.MF ./";
            Process p = Runtime.getRuntime().exec(command,null,dir);
            p.waitFor();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        //将同名文件夹下的jar包复制到指定位置
        try{
            String command="cmd /c "+"copy  "+"\""+ baseConfig.getUploadedFilePath()+sub+"\\"+fileName+"\"  \""+ baseConfig.getInstrumentationPath()+"\"";
            Process p = Runtime.getRuntime().exec(command,null,dir);
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
