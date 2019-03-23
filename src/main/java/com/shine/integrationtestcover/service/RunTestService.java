package com.shine.integrationtestcover.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: WHQ
 * @Date: 2019/3/23 19:36
 */
@Service
public class RunTestService {

        //project jar
        private String jarpath="";
        private String jarname="";

        //test .java
        private String javafilepath="";
        private String javafilename="";

        //test way(JUnit)
        private String testwaypath="";
        private String testwayname="";

        public String getJarpath() {
            return jarpath;
        }

        public void setJarpath(String jarpath) {
            this.jarpath = jarpath;
        }

        public String getJarname() {
            return jarname;
        }

        public void setJarname(String jarname) {
            this.jarname = jarname;
        }

        public String getJavafilepath() {
            return javafilepath;
        }

        public void setJavafilepath(String javafilepath) {
            this.javafilepath = javafilepath;
        }

        public String getJavafilename() {
            return javafilename;
        }

        public void setJavafilename(String javafilename) {
            this.javafilename = javafilename;
        }

        public String getTestwaypath() {
            return testwaypath;
        }

        public void setTestwaypath(String testwaypath) {
            this.testwaypath = testwaypath;
        }

        public String getTestwayname() {
            return testwayname;
        }

        public void setTestwayname(String testwayname) {
            this.testwayname = testwayname;
        }


        /*
        Delete package declaration
         */
        public void handleJavaFile(){
            try{
                File file=new File(javafilepath+"//"+javafilename+".java");
                System.out.println();
                System.out.println(file.getAbsolutePath());
                if(file.exists())
                    System.out.println("exit" +file.getPath());
                else
                    System.out.println("not exist");

                BufferedReader br=new BufferedReader(new FileReader(file.getAbsolutePath()));
                String str=null;
                int num=0;
                int line=1;
                List list = new ArrayList();
                while((str=br.readLine())!=null){
                    ++num;
                    System.out.println(num+"行："+str);
                    if( num == line )
                        continue;
                    list.add(str);
                }
                BufferedWriter bw=new BufferedWriter(new FileWriter(file.getAbsolutePath()));
                for(int i=0;i<list.size();i++){
                    bw.write(list.get(i).toString());
                    bw.newLine();
                }
                bw.flush();
                bw.close();
                br.close();

            }catch (Exception e){
                e.printStackTrace();
            }

        }

        /*
        console command:.java to .class
         */
        public void compileJava(){
            try {
                //String command="javac -cp C://Users//22831//Desktop//Dynamic//target//com.dynamic-1.0-SNAPSHOT.jar;C://Users//22831//Desktop//Dynamic//target//junit-4.10.jar C://Users//22831//Desktop//Dynamic//src//test//java//test1.java";
                String command = "javac -cp " + jarpath + "//" + jarname + ";" + testwaypath + "//" + testwayname + " " + javafilepath + "//" + javafilename+".java";
                System.out.println(command);
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        /*
        invoke methods
         */
        public void invokeMethod(){
            try {
                //  URL url = new URL("file:C://Users//22831//Desktop//lib//com.dynamic-1.0-SNAPSHOT.jar");
                URL url = new URL("file:"+jarpath+"//"+jarname);
                System.out.println(url);
                //   URLClassLoader loader= new URLClassLoader(new URL[] { url }, Thread.currentThread().getContextClassLoader());
                // File xFile=new File("C://Users//22831//Desktop");
                File xFile=new File(javafilepath);
                URL url2 = xFile.toURL();
                URLClassLoader ClassLoader=new URLClassLoader(new URL[]{ url2,url });
                Class xClass=ClassLoader.loadClass(javafilename);
                Object xObject=xClass.newInstance();
                Method[] method=xClass.getDeclaredMethods();
                for(Method m:method){
                    System.out.println(m.getName());
                }
                Method xMethod=xClass.getDeclaredMethod("test1");//先定义跑里面的一个方法
                xMethod.setAccessible(true);
                xMethod.invoke(xClass.newInstance());


            }catch (Exception e){
                e.printStackTrace();
            }

        }


    }
