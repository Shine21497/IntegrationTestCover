package com.shine.integrationtestcover.regressiontest;

import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;
import jdk.internal.org.objectweb.asm.tree.InsnList;
import jdk.internal.org.objectweb.asm.tree.JumpInsnNode;

import java.util.*;

public class Node {
    private String name;
    private List<Edge> edgeList = new ArrayList<Edge>();
    private InsnList insnList;

    public Node(String n, InsnList insnList){
        name = n;
        this.insnList = insnList;
    }
    public void addEdge(Edge edge){
        if(!edgeList.contains(edge)) {
            edgeList.add(edge);
        }
    }
    public int getEdgeListSize(){
        return edgeList.size();
    }
    public Edge getEdge(int i){
        return edgeList.get(i);
    }
    public boolean containsEdge(Edge edge){
        if(edgeList.contains(edge))
            return true;
        else
            return false;
    }
    public void setName(String n){
        name = n;
    }
    public String getName(){
        return name;
    }
    public boolean compareContent(Node anotherNode) {
        InsnList anotherList = anotherNode.insnList;
        if(anotherList.size() != this.insnList.size()) {
            return false;
        }
        for(int i = 0; i < this.insnList.size(); i++ ) {
            if(!this.insnList.get(i).getClass().equals(anotherList.get(i).getClass())) {
                return false;
            }
            if (CompareUtil.compareInsnNode(this.insnList.get(i), anotherList.get(i))){
                continue;
            } else {
                System.out.println("different ins" + i);
                System.out.println(this.insnList.get(i).getClass());
                /*if(this.insnList.get(i) instanceof JumpInsnNode) {
                    System.out.println(((JumpInsnNode)this.insnList.get(i)).label.getLabel().getOffset());
                    System.out.println(((JumpInsnNode)anotherList.get(i)).label.getLabel().);
                }*/
                return false;
            }
        }
        return true;
    }

    public InsnList getInsnList() {
        return insnList;
    }
}
