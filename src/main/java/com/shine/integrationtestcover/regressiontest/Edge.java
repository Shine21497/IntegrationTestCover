package com.shine.integrationtestcover.regressiontest;

/**
 * @Author: Shine
 * @Date: 2019/5/8
 */
public class Edge {
    private String dest;
    private String src;

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }


    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public Edge(String src, String dest) {
        this.src = src;
        this.dest = dest;
    }

    @Override
    public boolean equals(Object o) {
        if(o != null && (o instanceof Edge)) {
            Edge otherEdge = (Edge)o;
            return this.src.equals(otherEdge.src) && this.dest.equals(otherEdge.dest);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() + this.dest.hashCode() + this.src.hashCode();
    }

    @Override
    public String toString() {
        return src + " CALL " + dest;
    }


}
