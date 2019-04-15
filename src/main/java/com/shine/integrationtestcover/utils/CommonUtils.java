package com.shine.integrationtestcover.utils;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * @Author: Shine
 * @Date: 2019/4/14
 */
@Component
public class CommonUtils {

    public boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null && children.length > 0) {
                // 递归删除目录中的子目录下
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    public void copyFile(String fileName, String sourcePath, String destPath) {
        File file = null;
        if (sourcePath != null && !"".equals(sourcePath)) {
            file = sourcePath.endsWith("\\") ? new File(sourcePath + fileName) : new File(sourcePath + "\\" + fileName);
            if (!file.exists()) {
                System.out.println("无法找到源文件目录[" + sourcePath + "]或源文件[" + fileName + "]不存在，无法完成复制！");
            }
        }
        File destFilePath = new File(destPath);
        if (!destFilePath.exists()) {
            destFilePath.mkdirs();
            System.out.println("成功创建目录" + destPath);
        }
        StringBuilder cmd = new StringBuilder("cmd.exe /c copy ");//关于java调用cmd的参数可以自行百度
        cmd.append(sourcePath + "\\" + fileName + " ").append(destPath + " ");
        try {
            Process process = Runtime.getRuntime().exec(cmd.toString());
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(cmd.toString());
    }
    //把sourceDic下所有文件包括文件夹复制到destDic，不是把sourceDic复制过去,sourcedic结尾不带\ destdic带有\
    public void copyDic(String sourceDic, String destDic) {
        if(sourceDic.endsWith("/")) {
            sourceDic = sourceDic.substring(0, sourceDic.length() - 1);
        }
        File file = null;
        if (sourceDic != null && !"".equals(sourceDic)) {
            file = new File(sourceDic);
            if (!file.exists()) {
                System.out.println("无法找到源文件目录不存在，无法完成复制！");
            }
        }
        File destFilePath = new File(destDic);
        if (!destFilePath.exists()) {
            destFilePath.mkdirs();
            System.out.println("成功创建目录" + destDic);
        }
        StringBuilder cmd = new StringBuilder("cmd.exe /c xcopy ");//关于java调用cmd的参数可以自行百度
        cmd.append(sourceDic.replaceFirst("/", "").replace("/", "\\") + " ").append(destDic.replaceFirst("/", "").replace("/", "\\") + " ");
        cmd.append("/e /y");
        try {
            Process process = Runtime.getRuntime().exec(cmd.toString());
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(cmd.toString());
    }
}
