package com.shine.integrationtestcover.service.programInstrument;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {
    public void testmy() {
        System.out.println(this.getClass());
    }
    public static  boolean mkDirectory(String path){
        File file = null;
        try {
            file = new File(path);
            if (!file.exists()) {
                return file.mkdirs();
            }
            else{
                return false;
            }
        } catch (Exception e) {
        } finally {
            file = null;
        }
        return false;
    }
    public static void main(String[] args) throws IOException {
        Test test = new Test();
        test.testmy();
    }
}
