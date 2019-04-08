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

    private static int task = 0;//完成的方法数

    private List<String> allmethods = new LinkedList<>();

    private List runresults = new LinkedList();

    private List runprocess = new LinkedList();

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());//测试异步

    //project jar
    private String jarpath = "";
    private String jarname = "";//不带jar

    //test .java
    private String javafilepath = "";

    //test way(JUnit)
   // private String testwaypath = "C://Users//22831//Desktop//target";
    private String testwaypath = "";
    private String testwayname = "junit-4.10";

    public List<String> getAllmethods() {
        return allmethods;
    }

    public int getTask() {
        return task;
    }

    //初始化，接收项目名称
    public void initate(String projectname) {
        this.setTestwaypath(baseConfig.getUploadedFilePath());
        this.setJarpath(baseConfig.getUploadedTestPath(projectname));//插桩后的位置
        this.setJarname(projectname);
        this.setJavafilepath(baseConfig.getUploadedTestPath(projectname));//测试文件位置
        // this.setJarpath("C://Users//22831//Desktop//target//demo");//测试：插桩后的位置
        // this.setJavafilepath("C://Users//22831//Desktop//target//demo");//测试：测试文件位置
    }

    /*
    读出一个testfile目录下的的java文件的包名，默认第一行是包名，匹配,返回包名,形式为'com.example.demo.controller'
     */
    public String getPackagename(String javafilename) {
        String packagename = "";
        try {
            File file = new File(javafilepath + "//" + javafilename + ".java");
            //System.out.println(file.getAbsolutePath());
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
        return packagename.replace("package ", "").replace(";", "");
    }

    /*
    文件复制
     */
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }


    /*
    在某个位置建和包名一样的文件夹,并且复制java文件到对应包名下面,返回现在的java文件的path
     */
    public String handlePackageName(String javafilename) {
        String packagename = getPackagename(javafilename);
        String[] names = packagename.split("\\.");
        String path = this.javafilepath + "//";//指定父目录(改),测试文件的位置
        for (int i = 0; i < names.length; i++) {
            if (i < names.length)
                path = path + names[i] + "/";
            else {
                path += names[i];
            }
        }
        File file = new File(path);
        try {
            file.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        File javaFile = new File(this.getJavafilepath() + "//" + javafilename + ".java");
        // System.out.println(this.getJavafilepath() + "//" + this.getJavafilename() + ".java");
        //if (javaFile.exists()) System.out.println("find success");
        //else System.out.println("find failure");
        //javaFile.renameTo(new File(path + "//" + javafilename + ".java"));
        copyFile(this.javafilepath + "//" + javafilename + ".java", path + "//" + javafilename + ".java");
        return path;
    }


    /*
    编译java文件,需要把java文件复制到新建的包目录下面，
     */
    public void compileJava(String javafilename) {
        String packagename = getPackagename(javafilename).replace(".", "//");
        handlePackageName(javafilename);
        try {
            // System.out.println(javafilepath + "//" + packagename);
            //String command=
            // "javac -cp C:\Users\22831\Desktop\lib\IntegrationTestCover.jar;C:\Users\22831\Desktop\lib\junit-4.10.jar com\shine\integrationtestcover\service\GraphServiceTest.java";
            String command = "javac -cp " + jarpath + "//" + jarname + ".jar" + ";" + testwaypath + "//" + testwayname + ".jar" + " " + javafilepath + "//" + packagename + "//" + javafilename + ".java";
            // System.out.println(command);
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
        String packagename = getPackagename(javafilename);
        List<String> methods = new LinkedList<>();
        try {
            File file = new File(this.getJarpath() + "//" + this.getJarname() + ".jar");//加载外部jar包
            URL url = file.toURI().toURL();
            File xFile = new File(this.getJavafilepath());
            if (xFile != null) System.out.println("success");
            URL url2 = xFile.toURL();
            URLClassLoader ClassLoader = new URLClassLoader(new URL[]{url2, url});
            Class xClass = ClassLoader.loadClass(packagename + "." + javafilename);//一个java文件
            // Class xClass = ClassLoader.loadClass("com.example.demo.controller.TestMethod");
            Method[] method = xClass.getDeclaredMethods();
            for (Method m : method) {
                //获得java文件的所有方法
                //System.out.println(m.getName());
                methods.add(m.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return methods;

    }

    /*
    invoke methods:filename是java文件的名字,invoke 一个java文件的某个方法,返回调用关系
     */
    public List<String> invokeMethod(String javafilename, String methodname) {
        String packagename = getPackagename(javafilename);
        System.out.println(packagename);
        compileJava(javafilename);
        //String packagename="com.example.demo.controller";
        PrintStream old = System.out;
        try {
            //File file = new File("C://Users//22831//Desktop//lib//demo.jar");
            File file = new File(this.getJarpath() + "//" + this.getJarname() + ".jar");
            //URL url = new URL("file:"+jarpath+"//"+jarname);
            URL url = file.toURI().toURL();
            //  System.out.println("url" + url);
            //   URLClassLoader loader= new URLClassLoader(new URL[] { url }, Thread.currentThread().getContextClassLoader());
            // File xFile=new File("C://Users//22831//Desktop");
            File xFile = new File(this.javafilepath);
            URL url2 = xFile.toURL();
            URLClassLoader ClassLoader = new URLClassLoader(new URL[]{url2, url});
            //Class xClass = ClassLoader.loadClass("com.example.demo.controller.TestMethod");
            Class xClass = ClassLoader.loadClass(packagename + "." + javafilename);
            Method[] method = xClass.getDeclaredMethods();
            FileOutputStream bos = new FileOutputStream(this.javafilepath + "//output-" + javafilename + ".txt");
            System.setOut(new PrintStream(bos));
            //Method xMethod = xClass.getDeclaredMethod("te11");//先定义跑里面的一个方法
            Method xMethod = xClass.getDeclaredMethod(methodname);
            xMethod.setAccessible(true);
            xMethod.invoke(xClass.newInstance());

        } catch (Exception e) {
            e.printStackTrace();
        }
        //重定向到控制台
        System.setOut(old);

        List<String> methodsrelationship = new LinkedList<>();

        //读文件内容
        try {
            String filepath = this.javafilepath + "//output-" + javafilename + ".txt";
            File file = new File(filepath);
            if (file != null) System.out.println("find la");
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.matches(".*CALL.*")) {
                    methodsrelationship.add(line.replace("/", "."));
                }
            }
            br.close();
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        this.runresults = methodsrelationship;
        return methodsrelationship;
    }

    /*
    跑一个java文件下的所有的测试用例，java文件名字,异步,返回两个数值
     */
    @Async
    public List runTest(String javafilename) {
        // compileJava(javafilename);
        allmethods = new LinkedList<>();
        task = 0;
        List<String> sumtask = getMethods(javafilename);//java文件里面所有的方法
        try {
            File file = new File(this.jarpath + "//" + this.jarname + ".jar");//加载外部jar包
            URL url = file.toURI().toURL();

            File xFile = new File(this.javafilepath);
            URL url2 = xFile.toURL();
            URLClassLoader ClassLoader = new URLClassLoader(new URL[]{url2, url});

            for (int i = 0; i < sumtask.size(); i++) {
                List<String> now = invokeMethod(javafilename, sumtask.get(i));
                if (now.size() > 0 && now != null) {
                    allmethods.addAll(now);
                }

                task++;
                logger.info("=============" + Thread.currentThread().getName() + "异步");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        this.runresults = allmethods;
        List finishtask = new LinkedList();
        finishtask.add(task);
        finishtask.add(sumtask.size());
        this.runprocess = finishtask;
        return finishtask;

    }

    /*
    跑项目下面的所有test文件,返回n和m两个数
    */
    @Async
    public List runAll() throws Exception {
        task = 0;
        int sumtask = 0;
        List results = new LinkedList<>();
        String path = this.javafilepath;
        //    System.out.println("pppp"+path);
        File file = new File(path);
        File[] tempList = file.listFiles();
        // System.out.println(tempList.length);
        for (File f : tempList) {
            if (f.getName().contains(".java")) {
                //  System.out.println("pppp" + f.getName());
                int m = getMethods(f.getName().replace(".java", "")).size();
                sumtask += m;
            }
        }
        //  System.out.println("all" + sumtask);
        for (File f : tempList) {
            if (f.getName().contains(".java")) {
                String filename = f.getName().replace(".java", "");
                // System.out.println("zibi1" + filename);
                List<String> m = getMethods(filename);
                for (int i = 0; i < m.size(); i++) {
                    System.out.println("zibi2" + m.get(i));
                }
                for (int i = 0; i < m.size(); i++) {
                    results.addAll(invokeMethod(filename, m.get(i)));
                    Thread.sleep(5000);
                    task++;
                }
            }
        }
        logger.info("=============" + Thread.currentThread().getName() + "异步");
        this.runresults = results;
        //System.out.println(results.size() + "zibiiiiiiii");
        List finishtask = new LinkedList();

        finishtask.add(task);
        finishtask.add(sumtask);
        this.runprocess = finishtask;
        return finishtask;
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

    public void setTestwaypath(String testwaypath) {
        this.testwaypath = testwaypath;
    }

    public void setTestwayname(String testwayname) {
        this.testwayname = testwayname;
    }

    public List getRunresults() {
        return runresults;
    }

    public void setRunresults(List runresults) {
        this.runresults = runresults;
    }

    public List getRunprocess() {
        return runprocess;
    }

    public void setRunprocess(List runprocess) {
        this.runprocess = runprocess;
    }
}
