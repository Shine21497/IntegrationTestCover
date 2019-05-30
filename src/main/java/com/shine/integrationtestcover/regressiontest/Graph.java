package com.shine.integrationtestcover.regressiontest;

import com.shine.integrationtestcover.config.BaseConfig;

import java.io.*;
import java.util.*;

public class Graph {

    private HashMap<String,Node> nodeMap = new HashMap<>();

    public boolean addNode(String key, Node node) {
        this.nodeMap.put(key, node);
        return true;
    }

    public boolean ifNodeExist(String key) {
        return nodeMap.containsKey(key);
    }

    public Node getNode(String key) {
        return nodeMap.get(key);
    }

    public HashMap<String, Node> getNodeMap() {
        return nodeMap;
    }

    private static Graph newGraph = new Graph();
    private static Graph oldGraph = new Graph();
    private  List<String> visitedList=new ArrayList<String>();

    public List<Edge> getDangerousList() {
        return dangerousList;
    }

    public List<String> getDifferentNodeKey() {
        return differentNodeKey;
    }

    public List<String> getNewNodeKey() {
        return newNodeKey;
    }

    private  List<Edge> dangerousList=new ArrayList<Edge>();
    private  List<String> differentNodeKey = new ArrayList<String>();
    private  List<String> newNodeKey = new ArrayList<>();

    public List<String> getDeleteNodeKey() {
        return deleteNodeKey;
    }

    private  List<String> deleteNodeKey=new ArrayList<>();

    public  void initNodeMap(String oldJarName,String newJarName,String packageName){
        BaseConfig baseConfig = new BaseConfig();
//        PaserJar paserJar = new PaserJar(baseConfig.getRegressionFilePath(), oldJarName, oldGraph);
        PaserJar paserJar = new PaserJar(baseConfig.getUploadedFilePath(), oldJarName, oldGraph);
        paserJar.setPackageName(packageName);
        oldGraph = paserJar.getInvoking();
        PaserJar paserJarNew = new PaserJar(baseConfig.getRegressionFilePath(), newJarName, newGraph);
        paserJarNew.setPackageName(packageName);
        newGraph = paserJarNew.getInvoking();
    }
    public  Edge match(Node n,Edge edge){
        for(int i = 0; i < n.getEdgeListSize(); i++){
            Edge e = n.getEdge(i);
            if(e.equals(edge)){
                return e;
            }
        }
        return null;
    }

    public  void compare(Node oldNode,Node newNode){
        //进入compare 一定是 content一致

        //遍历newNode中所有的边
        for(int i = 0; i < newNode.getEdgeListSize(); i++){
            Edge edge = newNode.getEdge(i);
            Edge e = match(oldNode,edge);
            if(e == null) {
                System.out.println("新增了一个调用 something unexpected");
                System.out.println(edge);
                continue;
            }
            Node oldC = oldGraph.getNode(e.getDest());
            Node newC = newGraph.getNode(e.getDest());
            if(newC == null) {
                continue;
            }
            if(differentNodeKey.contains(newC.getName())){
                dangerousList.add(e);
            } else if(visitedList.contains(newC.getName())){
                continue;
            } else {
                visitedList.add(newC.getName());
                if(!oldC.compareContent(newC)) {
                    differentNodeKey.add(newC.getName());
                } else {
                    compare(oldC,newC);
                }
            }
        }
        for(int i=0;i<oldNode.getEdgeListSize();i++){
            Edge edge = oldNode.getEdge(i);
            if(!newNode.containsEdge(edge)) {
                System.out.println("旧的调用被删了 something unexpected");
                System.out.println(edge);
                dangerousList.add(edge);
            }

        }
    }
    public  void Graph(String oldJarName,String newJarName,String packageName) {
        initNodeMap(oldJarName,newJarName,packageName);
        for (Map.Entry<String, Node> entry : newGraph.getNodeMap().entrySet()){
            if(!visitedList.contains(entry.getKey())) {
                String key = entry.getKey();
                visitedList.add(key);
                Node newC = newGraph.getNode(key);
                if (oldGraph.ifNodeExist(key)) {
                    Node oldC = oldGraph.getNode(key);
                    if (!oldC.compareContent(newC)) {
                        differentNodeKey.add(oldC.getName());
                        continue;
                    }
                    compare(oldC, newC);
                } else {
                    //新增的结点
                    newNodeKey.add(key);
                    continue;
                }
            }
        }
        for(String key : oldGraph.getNodeMap().keySet()){
            if(!newGraph.ifNodeExist(key)){
                deleteNodeKey.add(key);
                continue;
            }
        }
//        System.out.println("dangerous edge");
//        for(int i=0;i<dangerousList.size();i++){
//            System.out.println(dangerousList.get(i));
//        }
//
//        System.out.println("dangerous node");
//        for(int i=0;i<differentNodeKey.size();i++){
//            System.out.println(differentNodeKey.get(i));
//        }
//
//        System.out.println("new node");
//        for(int i=0;i<newNodeKey.size();i++){
//            System.out.println(newNodeKey.get(i));
//        }


}
}
