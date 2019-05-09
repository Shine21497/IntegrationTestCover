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
    public static List<Node> visitedList=new ArrayList<Node>();
    public static List<Edge> dangerousList=new ArrayList<Edge>();
    public static List<String> differentNodeKey = new ArrayList<>();

    public static void initNodeMap(){
        BaseConfig baseConfig = new BaseConfig();
        Graph oldGraph = new Graph();
        PaserJar paserJar = new PaserJar(baseConfig.getRegressionFilePath(), "bean-query.jar", oldGraph);
        oldGraph = paserJar.getInvoking();
    }
    public static Edge match(Node n,Edge edeg){
        for(int i = 0; i < n.getEdgeListSize(); i++){
            Edge e = n.getEdge(i);
            if(e.equals(edeg)){
                return e;
            }
        }
        return null;
    }
    public static boolean nodesEquiv(Node oldC,Node newC){
        //判断
        return true;
    }
    public static void compare(Node oldNode,Node newNode){
        //比较自身
        //将oldN加入VisitedList
        visitedList.add(oldNode);
        //遍历newNode中所有的边
        for(int i=0;i<newNode.getEdgeListSize();i++){
            Edge edge = newNode.getEdge(i);
            Edge e = match(oldNode,edge);
            if(e == null) {
                System.out.println("something unexpected");
                continue;
            }
            Node oldC = oldGraph.getNode(e.getDest());
            Node newC = newGraph.getNode(e.getDest());
            if(!nodesEquiv(oldC,newC)){
                dangerousList.add(e);
                differentNodeKey.remove(e.getDest());
            }
            else if(visitedList.contains(oldC)){
                compare(oldC,newC);
            }
        }
        for(int i=0;i<oldNode.getEdgeListSize();i++){
            Edge edge = oldNode.getEdge(i);
            if(!newNode.containsEdge(edge)) {
                dangerousList.add(edge);
                differentNodeKey.remove(edge.getDest());
            }

        }
    }
    public static void main(String[] args) {
        initNodeMap();
        for (Map.Entry<String, Node> entry : newGraph.getNodeMap().entrySet()){
            String key = entry.getKey();
            Node newC = newGraph.getNode(key);
            if(oldGraph.ifNodeExist(key)){
                Node oldC = oldGraph.getNode(key);
                if(!nodesEquiv(oldC, newC)) {
                    differentNodeKey.add(oldC.getName());
                    continue;
                }
                compare(oldC,newC);
            }
            else
                continue;
        }
        for(int i=0;i<dangerousList.size();i++){
            System.out.println(dangerousList.get(i));
        }
        System.out.println("visitedList:");
        for(int i=0;i<visitedList.size();i++){
            System.out.println(visitedList.get(i).getName());
        }
}
}
