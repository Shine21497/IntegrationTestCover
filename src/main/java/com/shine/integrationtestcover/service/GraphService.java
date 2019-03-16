package com.shine.integrationtestcover.service;

import com.shine.integrationtestcover.service.graphCustom.GraphAlo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GraphService {

    @Autowired
    private ParseJarService parseJarService;

    private String path;
    private String filename;
    private  ArrayList<String> vertexResult;
    private  ArrayList<String> result;
    private List<String> invokeString;

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public ArrayList<String> getVertexResult() {
        return vertexResult;
    }
    public void setVertexResult(ArrayList<String> vertexResult) {
        this.vertexResult = vertexResult;
    }

    //获得调用string
    public void parse(){
        parseJarService.setFilename(filename);
        parseJarService.setPath(path);
        this.invokeString= parseJarService.getInvoking();
    }
    public void initiate(){
        parse();
        result=new ArrayList<String>();
        ArrayList<String> vertex=new ArrayList<String>();
        for(int i=0;i<invokeString.size();i++){
            result.add(invokeString.get(i));
        }

        for(String temp:result){
            String[] tempList=temp.split(" ");
            vertex.add(tempList[0]);
            vertex.add(tempList[2]);
        }
        vertexResult = new ArrayList<String>(new HashSet<String>(vertex));//


    }

    //获取所有节点名称
    public ArrayList<List> getEdges(){

        int vertexNum=vertexResult.size();
        GraphAlo graph = new GraphAlo(vertexNum);

        for(int i=0;i<vertexResult.size();i++) {
            graph.insertVertex(vertexResult.get(i));
        }

        for (String temp : result) {
            String[] tempList = temp.split(" ");
            int startVertex = vertexResult.indexOf(tempList[0]);
            int endVertex = vertexResult.indexOf(tempList[2]);
            graph.insertEdge(startVertex, endVertex, 1);
        }
        ArrayList<List> edges=new ArrayList<>();
        for(int i=0;i<vertexNum;i++){
            for(int j=0;j<vertexNum;j++){
                if(graph.getWeight(i,j)==1){
                   List content=new LinkedList();
                    content.add(i);
                    content.add(j);
                    edges.add(content);

                }
            }
        }
        System.out.println(edges);
        return edges;

    }


}
