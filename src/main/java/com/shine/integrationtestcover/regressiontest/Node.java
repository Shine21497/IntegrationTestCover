package com.shine.integrationtestcover.regressiontest;

import java.util.*;

public class Node {
    private String name;
    private List<Edge> edgeList = new ArrayList<Edge>();

    public Node(String n){
        name = n;
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

}
