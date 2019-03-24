package com.shine.integrationtestcover.service.codeParse;


import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**parse jar file class
 * Created by shikun on 2017/2/24.
 */

public class ClassVisitor extends EmptyVisitor{
    private JavaClass clazz;
    private ConstantPoolGen constants;
    private String classReferenceFormat;
    private List<String> classCallRelationship;

    public List<String> getCallRelationship() {
        return classCallRelationship;
    }

    public ClassVisitor(JavaClass jc) {
        clazz = jc;
        constants = new ConstantPoolGen(clazz.getConstantPool());
        classReferenceFormat = "C:" + clazz.getClassName() + " %s";
        classCallRelationship = new LinkedList<>();
    }

    public void visitJavaClass(JavaClass jc) {
        jc.getConstantPool().accept(this);
        Method[] methods = jc.getMethods();
        for (int i = 0; i < methods.length; i++)
            methods[i].accept(this);
    }

    MethodVisitor visitor;
    public void visitMethod(Method method) {
        MethodGen mg = new MethodGen(method, clazz.getClassName(), constants);
        visitor = new MethodVisitor(mg, clazz);
        visitor.start();
    }

    public void start() {
        visitJavaClass(clazz);
        classCallRelationship = visitor.getCallRelationship();
        System.out.println("ClassCallRelationship  size  is:"+classCallRelationship.size());

    }


}
