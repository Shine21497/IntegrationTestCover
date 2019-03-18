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
    public ArrayList<HashMap<String, Object>> getVertex() {
        ArrayList<HashMap<String, Object>> result = new ArrayList<>();
        for(int i = 0; i < this.vertexResult.size(); i++){
            HashMap<String, Object> node = new HashMap<>();
            node.put("name", vertexResult.get(i));
            node.put("type", (new Random()).nextInt(3));
            result.add(node);
        }
        return result;
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
       // System.out.println("invokeString" +invokeString.size());
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
        System.out.println("vertexResult:"+vertexResult.size());


    }

    //获取所有边
    public ArrayList<HashMap> getEdges(){

        int numOfEdge=0;

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
        ArrayList<HashMap> edges=new ArrayList<>();
        for(int i=0;i<vertexNum;i++){
            for(int j=0;j<vertexNum;j++){
                if(graph.getWeight(i,j)==1){
                    numOfEdge++;
                   HashMap<String, Integer> content=new HashMap();
                    content.put("source", i);
                    content.put("target", j);
                    edges.add(content);

                }
            }
        }
        System.out.println("num"+numOfEdge);
        return edges;

    }


}
