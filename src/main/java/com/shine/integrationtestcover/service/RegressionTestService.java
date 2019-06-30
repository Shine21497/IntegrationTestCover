package com.shine.integrationtestcover.service;

import com.shine.integrationtestcover.config.BaseConfig;
import com.shine.integrationtestcover.regressiontest.Edge;
import com.shine.integrationtestcover.regressiontest.Graph;
import com.shine.integrationtestcover.regressiontest.Node;
import com.shine.integrationtestcover.regressiontest.PaserJar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Shine
 * @Date: 2019/5/29
 */
@Service
public class RegressionTestService {
    @Autowired
    RunTestService runTestService;

    public Graph newGraph = new Graph();
    public Graph oldGraph = new Graph();
    public List<String> visitedList=new ArrayList<String>();
    public List<Edge> dangerousList=new ArrayList<Edge>();
    public List<String> differentNodeKey = new ArrayList<>();
    public List<String> newNodeKey = new ArrayList<>();
    public List<String> deleteNodeKey = new ArrayList<>();

    public void initNodeMap(String oldJarName,String newJarName,String packageName){
        String project = oldJarName.substring(0, oldJarName.indexOf("."));
        BaseConfig baseConfig = new BaseConfig();
        PaserJar paserJar = new PaserJar(baseConfig.getUploadedFilePath(), oldJarName, oldGraph);
        paserJar.setPackageName(packageName);
        oldGraph = paserJar.getInvoking();
        PaserJar paserJarNew = new PaserJar(baseConfig.getRegressionFilePath()+"//"+project+"//", newJarName, newGraph);
        paserJarNew.setPackageName(packageName);
        newGraph = paserJarNew.getInvoking();

    }
    public Edge match(Node n, Edge edge){
        for(int i = 0; i < n.getEdgeListSize(); i++){
            Edge e = n.getEdge(i);
            if(e.equals(edge)){
                return e;
            }
        }
        return null;
    }

    public void compare(Node oldNode,Node newNode){
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

    public void getDangerousInfo(String oldJarName,String newJarName,String packageName) {
        initNodeMap(oldJarName, newJarName, packageName);
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
    }

    public List<String> regressiontest(String oldJarName,String newJarName,String packageName) throws Exception {
        getDangerousInfo(oldJarName, newJarName, packageName);
        List<String> result=new ArrayList<String>();
        System.out.println("dangerous edge");
        for(int i=0;i<dangerousList.size();i++){
            System.out.println(dangerousList.get(i).toString());
        }
        System.out.println("dangerous node");
        for(int i=0;i<differentNodeKey.size();i++){
            System.out.println(differentNodeKey.get(i));
        }
        System.out.println("new node");
        for(int i=0;i<newNodeKey.size();i++){
            System.out.println(newNodeKey.get(i));
        }
        System.out.println("delete node");
        for(int i=0;i<deleteNodeKey.size();i++){
            System.out.println(deleteNodeKey.get(i));
        }
        System.out.println("regressiveTest");
//        HashMap<String,List<String>> TestMap = runTestService.regressionCompare(oldJarName.split("\\.")[0]);
        HashMap<String,List<String>> TestMap=new HashMap<String,List<String>>();
        System.out.println(oldJarName.split("\\.")[0]);
        try{
            TestMap = runTestService.regressionCompare(oldJarName.split("\\.")[0]);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("regressiveTestEnd");
        for(Map.Entry<String,List<String>> entry:TestMap.entrySet()){
            List<String> temp=entry.getValue();
            for(int i=0;i<temp.size();i++){
                if(dangerousList.contains(temp.get(i))){
                    if(!result.contains(entry.getKey()))
                        result.add(entry.getKey());
                    break;
                }
                for(String node: temp.get(i).split(" ")){
                    if(node!="CALL"){
                        if(differentNodeKey.contains(node)){
                            if(!result.contains(entry.getKey()))
                                result.add(entry.getKey());
                            break;
                        }
                        if(newNodeKey.contains(node)){
                            if(!result.contains(entry.getKey()))
                                result.add(entry.getKey());
                            break;
                        }
                        if(deleteNodeKey.contains(node)){
                            if(!result.contains(entry.getKey()))
                                result.add(entry.getKey());
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

}
