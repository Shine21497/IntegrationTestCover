package com.shine.integrationtestcover.regressiontest;

import com.shine.integrationtestcover.service.codeParse.ClassVisitor;
import com.shine.integrationtestcover.service.codeParse.MethodVisitor;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.InsnList;
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import org.apache.bcel.classfile.ClassParser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

/**
 * @Author: Shine
 * @Date: 2019/5/8
 */
public class PaserJar {
    private String filename;
    private String path;
    private Graph graph;
    private String packageName = "";



    //    public static String packageName = "cn/jimmyshi";
    private String packageName="cn/jimmyshi";
    private ClassVisitor visitor;

    public PaserJar(String path, String filename, Graph graph) {
        this.path = path;
        this.filename = filename;
        this.graph = graph;
    }
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Graph getInvoking(){
        try {
            File f = new File(path+"//"+filename);
            if (!f.exists()) {
                System.err.println("Jar file " + filename + " does not exist");
            }
            JarFile jar = new JarFile(f);
            Enumeration<JarEntry> entries = jar.entries();
            try (JarInputStream in = new JarInputStream(new BufferedInputStream(new FileInputStream(f)))) {
                JarEntry entry;
                while ((entry = in.getNextJarEntry()) != null) {
                    String name = entry.getName();
                    if (!name.startsWith(packageName)) continue;
                    if (!name.endsWith(".class")) continue;
                    ClassNode classNode = new ClassNode();
                    ClassReader reader = new ClassReader(in);
                    reader.accept(classNode, ClassReader.SKIP_DEBUG);
                    //System.out.println("ClassAAA" + classNode.name);
                    List<MethodNode> methodNodes = classNode.methods;
                    for (MethodNode methodNode : methodNodes) {
                        Node methodGraphNode;
                        if (this.graph.ifNodeExist(classNode.name + methodNode.name + methodNode.desc)) {
                            methodGraphNode = graph.getNode(classNode.name + ":"+methodNode.name + methodNode.desc);
//                        if (this.graph.ifNodeExist(classNode.name +":"+ methodNode.name)) {
//                            methodGraphNode = graph.getNode(classNode.name +":"+ methodNode.name);
                        } else {
                            methodGraphNode = new Node(classNode.name + ":"+methodNode.name + methodNode.desc, methodNode.instructions);
//                            methodGraphNode = new Node(classNode.name +":"+methodNode.name,methodNode.instructions);
                            this.graph.addNode(classNode.name + ":"+methodNode.name + methodNode.desc, methodGraphNode);
//                            this.graph.addNode(classNode.name +":"+ methodNode.name, methodGraphNode);
                        }
                        InsnList insnList = methodNode.instructions;
                        for (int i = 0; i < insnList.size(); ++i) {
                            if(insnList.get(i) instanceof MethodInsnNode) {
                                MethodInsnNode methodInsnNode = (MethodInsnNode)insnList.get(i);
                                //System.out.println(classNode.name + methodNode.name + methodNode.desc+ "CALL" + methodInsnNode.owner + methodInsnNode.name + methodInsnNode.desc);
                                methodGraphNode.addEdge(new Edge(classNode.name + ":"+methodNode.name + methodNode.desc, methodInsnNode.owner + ":"+methodInsnNode.name + methodInsnNode.desc));
//                                methodGraphNode.addEdge(new Edge(classNode.name + ":"+methodNode.name, methodInsnNode.owner +":"+ methodInsnNode.name));
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error while processing jar: " + e.getMessage());
            e.printStackTrace();
        }
        return this.graph;
    }

}
