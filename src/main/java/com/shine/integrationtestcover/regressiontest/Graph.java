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

    public static Graph newGraph = new Graph();
    public static Graph oldGraph = new Graph();
    public static List<String> visitedList=new ArrayList<String>();
    public static List<Edge> dangerousList=new ArrayList<Edge>();
    public static List<String> differentNodeKey = new ArrayList<>();
    public static List<String> newNodeKey = new ArrayList<>();

    public static void initNodeMap(){
        BaseConfig baseConfig = new BaseConfig();
        PaserJar paserJar = new PaserJar(baseConfig.getRegressionFilePath(), "bean-query.jar", oldGraph);
        oldGraph = paserJar.getInvoking();
        PaserJar paserJarNew = new PaserJar(baseConfig.getRegressionFilePath(), "bean-query-after-change.jar", newGraph);
        newGraph = paserJarNew.getInvoking();

    }
    public static Edge match(Node n,Edge edge){
        for(int i = 0; i < n.getEdgeListSize(); i++){
            Edge e = n.getEdge(i);
            if(e.equals(edge)){
                return e;
            }
        }
        return null;
    }

    public static void compare(Node oldNode,Node newNode){
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
    public static void main(String[] args) {
        initNodeMap();
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
        System.out.println("dangerous");
        for(int i=0;i<dangerousList.size();i++){
            System.out.println(dangerousList.get(i));
        }

}
}
