package com.shine.integrationtestcover.service.programInstrument;

import java.io.IOException;


import static com.shine.integrationtestcover.service.programInstrument.JarFileInput.jarFileInput;

public class Test {


    public static void main(String[] args) throws IOException {
        //输入jar包所在位置
        String path="C:\\Users\\acer\\Desktop\\xlabtest";
        String filename="junit-4.10.jar";
        jarFileInput(path,filename);

    }
}
