package com.shine.integrationtestcover.service.programInstrument;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {
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
    public static void main(String[] args) throws IOException {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        //输入jar包所在位置
        String path="C:\\Users\\acer\\Documents\\WeChat Files\\czczcz971220\\FileStorage\\File\\2019-04";
        String filename="demo.jar";
        int index=filename.lastIndexOf('.');
        String sub=filename.substring(0,index);
        System.out.println(sub);
        mkDirectory(path+"\\"+sub);
        File dir=new File(path+"\\"+sub);
        try {
            String command="cmd /c "+"copy  "+"\""+path+"\\"+filename+"\"  \""+path+"\\"+sub+"\"";
            String command1="jar -xvf "+filename;
            System.out.println(command);
            Process p = Runtime.getRuntime().exec(command);
            Process p2=Runtime.getRuntime().exec(command1,null,dir);

            BufferedReader br = new BufferedReader(new InputStreamReader(p2.getInputStream(), "GBK"));
                    String line = null;
                    while ((line = br.readLine()) != null) {

                        sb.append(line + "\n");
                    }
            BufferedReader br2 = new BufferedReader(new InputStreamReader(p2.getErrorStream(), "GBK"));
                    String line2 = null;
                    while ((line2 = br2.readLine()) != null) {
                        sb2.append(line2 + "\n");
                    }
                    p.waitFor();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(sb);
        System.out.println(sb2);
        JarFileInput.jarFileInput(path+"\\"+sub,filename);
        try {
            String command="cmd /c "+"del  \""+path+"\\"+sub+"\\"+filename+"\"";
            System.out.println(command);
            Process p = Runtime.getRuntime().exec(command);
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
            p.waitFor();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {

            String command="cmd /c "+"jar cvfm "+filename+" META-INF\\MANIFEST.MF ./";
            System.out.println(command);
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
            p.waitFor();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(sb);
        System.out.println(sb2);
    }
}
