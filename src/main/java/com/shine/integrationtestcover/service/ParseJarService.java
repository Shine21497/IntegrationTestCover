package com.shine.integrationtestcover.service;

import com.shine.integrationtestcover.service.codeParse.ClassVisitor;
import com.shine.integrationtestcover.service.codeParse.MethodVisitor;
import org.apache.bcel.classfile.ClassParser;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Service
public class ParseJarService {

    private String filename;
    private String path;
    public static String[] packageNames = {};

    private ClassVisitor visitor;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getInvoking(){
        ClassParser cp;
        try {
            File f = new File(path+"//"+filename);
            if (!f.exists()) {
                System.err.println("Jar file " + filename + " does not exist");
            }

            JarFile jar = new JarFile(f);

            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
                    continue;
                }
                if(!entry.getName().startsWith("java")) {
                    for (String packageName : packageNames) {
                        if (entry.getName().startsWith(packageName.replace(".", "/"))) {
                            cp = new ClassParser(path+"//"+filename, entry.getName());
                            visitor = new ClassVisitor(cp.parse());
                            visitor.start();
                        }
                    }
                }

            }
        } catch (IOException e) {
            System.err.println("Error while processing jar: " + e.getMessage());
            e.printStackTrace();
        }
        List<String> res = MethodVisitor.getCallRelationship();
        MethodVisitor.setCallRelationshipEmpty();
        return res;

    }

}



