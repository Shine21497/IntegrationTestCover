package com.shine.integrationtestcover.service;

import com.shine.integrationtestcover.config.BaseConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: WHQ
 * @Date: 2019/3/23 19:36
 */
@Service
public class RunTestService {

    @Autowired
    BaseConfig baseConfig;

    private static int task = 0;

    private static List<String> allmethods=new LinkedList<>();

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    //project jar
    private String jarpath = "";
    private String jarname = "";

    //test .java
    private String javafilepath = "";
   // private String javafilename = "";//不带'.java'

    //test way(JUnit)
    private String testwaypath = "";
    private String testwayname = "";

    private String packagename="";

    public static List<String> getAllmethods() {
        return allmethods;
    }

    public static void setAllmethods(List<String> allmethods) {
        RunTestService.allmethods = allmethods;
    }

    public int getTask() {
        return task;
    }

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

    //项目目录下的某个java文件
    public void initate(String projectname, String javafilename) {

        this.setJarname(projectname);//插桩的位置需要改一下的
        this.setTestwayname("junit-4.10.jar");
        this.setJarname(baseConfig.getUploadedFilePath());
        this.setTestwaypath(baseConfig.getUploadedFilePath());//测试包
        this.setJavafilepath(baseConfig.getUploadedTestPath(projectname));//测试文件位置

        handlePackageName(javafilename);
        compileJava(javafilename);//编译
    }

    /*
    读出一个当前目录的java文件的包名，默认第一行是包名，匹配,返回包名
     */
    public String getPackagename(String javafilename) {
        String packagename = "";
        try {
            File file = new File(javafilepath + "//" + javafilename + ".java");
            // System.out.println(file.getAbsolutePath());
            if (file.exists())
                System.out.println("exist file in" + file.getPath());
            else
                System.out.println("not exist");

            BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
            packagename = br.readLine();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return packagename.replace("package ","").replace(";","");
    }

    /*
    在某个位置建和包名一样的文件夹,并且移动java文件到对应包名下面,返回现在的java文件的path
     */
    public String handlePackageName(String javafilename) {
        String packagename=getPackagename(javafilename);
        String name1 = packagename.replace("package ", "");//去掉package
        String name = name1.replace(";", "");//去掉;
        this.packagename=name;
       // System.out.println("packagename"+name);
        // System.out.println(name);
        String[] names = name.split("\\.");
        String path = this.javafilepath+"//";//指定父目录(改),测试文件的位置
        for (int i = 0; i < names.length; i++) {
            if (i < names.length)
                path = path + names[i] + "/";
            else {
                path += names[i];
            }
        }
       // System.out.println("path"+path);
        File file = new File(path);
        try {
            file.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        File javaFile = new File(this.getJavafilepath() + "//" + javafilename + ".java");
        // System.out.println(this.getJavafilepath() + "//" + this.getJavafilename() + ".java");
        if (javaFile.exists()) System.out.println("find success");
        else System.out.println("find failure");
        javaFile.renameTo(new File(path + "//" + javafilename + ".java"));
        return path;
    }


    /*
    编译java文件,需要把java文件复制到新建的包目录下面，默认现在的java文件和jar包文件在一个目录下
     */
    public void compileJava(String javafilename) {
        //this.packagename="com.example.demo.controller";在getPackagename会赋值
        String packagename=this.packagename.replace(".","//");
        //String packagename="com//example//demo//controller.";
        try {
           // System.out.println(javafilepath + "//" + packagename);
            //String command=
            // "javac -cp C:\Users\22831\Desktop\lib\IntegrationTestCover.jar;C:\Users\22831\Desktop\lib\junit-4.10.jar com\shine\integrationtestcover\service\GraphServiceTest.java";
            String command = "javac -cp " + jarpath + "//" + jarname + ";" + testwaypath + "//" + testwayname + " " + javafilepath + "//" + packagename +"//"+ javafilename + ".java";
          //  System.out.println(command);
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    获得一个java文件里面的测试用例的方法名字
     */
    public List<String> getMethods(String javafilename) {
        //String packagename="com.example.demo.controller";
        String packagename=getPackagename(javafilename);
        List<String> methods = new LinkedList<>();
        try {
            File file = new File(this.getJarpath() + "//" + this.getJarname());//加载外部jar包
            URL url = file.toURI().toURL();
            File xFile = new File(this.getJavafilepath());
           // System.out.println(xFile.getAbsolutePath());
            if(xFile!=null) System.out.println("success");
            URL url2 = xFile.toURL();
            URLClassLoader ClassLoader = new URLClassLoader(new URL[]{url2, url});
            Class xClass = ClassLoader.loadClass(packagename + "." + javafilename);//一个java文件
           // Class xClass = ClassLoader.loadClass("com.example.demo.controller.TestMethod");
            Method[] method = xClass.getDeclaredMethods();
            for (Method m : method) {
                //获得java文件的所有方法
                System.out.println(m.getName());
                methods.add(m.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return methods;

    }

    /*
    invoke methods:filename是java文件的名字
     */
    public List<String> invokeMethod(String javafilename,String methodname) {

        String packagename=this.getPackagename(javafilename);
        //String packagename="com.example.demo.controller";
        PrintStream old = System.out;
        try {
            //File file = new File("C://Users//22831//Desktop//lib//demo.jar");
            File file = new File(this.getJarpath()+"//"+this.getJarname());
            //URL url = new URL("file:"+jarpath+"//"+jarname);
            URL url = file.toURI().toURL();
          //  System.out.println("url" + url);
            //   URLClassLoader loader= new URLClassLoader(new URL[] { url }, Thread.currentThread().getContextClassLoader());
            // File xFile=new File("C://Users//22831//Desktop");
            File xFile = new File(this.javafilepath);
            URL url2 = xFile.toURL();
            URLClassLoader ClassLoader = new URLClassLoader(new URL[]{url2, url});
            //Class xClass = ClassLoader.loadClass("com.example.demo.controller.TestMethod");
             Class xClass = ClassLoader.loadClass(packagename+"."+javafilename);
            Method[] method = xClass.getDeclaredMethods();
            FileOutputStream bos = new FileOutputStream("output-"+javafilename+".txt");
            System.setOut(new PrintStream(bos));
            //Method xMethod = xClass.getDeclaredMethod("te11");//先定义跑里面的一个方法
            Method xMethod = xClass.getDeclaredMethod(methodname);
            xMethod.setAccessible(true);
            xMethod.invoke(xClass.newInstance());

        } catch (Exception e) {
            e.printStackTrace();
        }
        //重定向到控制台
        // System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        System.setOut(old);

        List<String> methodsrelationship = new LinkedList<>();

        //读文件内容
        try {
            String filepath = "output-"+javafilename+".txt";
            File file = new File(filepath);
            if (file != null) System.out.println("find la");
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.matches(".*CALL.*")) {
                    methodsrelationship.add(line.replace("/","."));
                  //  System.out.println(line.replace("/","."));
                }
            }
            br.close();
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return methodsrelationship;

    }
    /*
    跑所有的测试用例
     */
    @Async
    public int runall() {
        allmethods=new LinkedList<>();
        task=0;
        try {
            File file = new File(this.getJarpath() + "//" + this.getJarname());//加载外部jar包
            URL url = file.toURI().toURL();

            //读项目的路径下面的测试用例的文件,现在是java文件和jar包在一个目录下
            File projectfile=new File(this.jarpath);
            File[] files=projectfile.listFiles();//获得目录下的所有文件
            for(File f:files){
                if(f.isFile()&&f.getName().matches(".*java*.")){
                    this.javafilepath=this.jarpath;
                    String javafilename=f.getName().replace(".java","");
                   // File file1=new File(this.jarpath+"/"+javafilename);
                    String packagename2=getPackagename(javafilename).replace("package","");
                    String packagename=packagename2.replace(";","");

                    handlePackageName(javafilename);
                    compileJava(javafilename);

                    List<String> methods=getMethods(javafilename);
                   // System.out.println(methods.size());

                    File xFile = new File(this.javafilepath);
                    URL url2 = xFile.toURL();
                    URLClassLoader ClassLoader = new URLClassLoader(new URL[]{url2, url});


                    for(int i=0;i<methods.size();i++){
                        List<String> now=new LinkedList<String>();
                        if(invokeMethod(javafilename,methods.get(i)).size()>0&&invokeMethod(javafilename,methods.get(i))!=null) {
                            now = invokeMethod(javafilename, methods.get(i));
                            if(now.size()>0&&now!=null) {
                                allmethods.addAll(now);
                            }
                        }
                        task++;
                        logger.info("=============" + Thread.currentThread().getName() + "异步");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return task;

    }


}
