package com.shine.integrationtestcover.controller;

import com.shine.integrationtestcover.config.BaseConfig;
import com.shine.integrationtestcover.service.ProgramInstrumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Author: Shine
 * @Date: 2019/3/17
 */
@RestController
public class FileUploadController {
    @Autowired
    private BaseConfig baseConfig;


    @RequestMapping(value = "/uploadJar")
    @ResponseBody
    public String uploadJar(@RequestParam("file") MultipartFile file){
        String result = "";
        if (!file.isEmpty()) {
            try {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(baseConfig.getUploadedFilePath() + file.getOriginalFilename())));
                System.out.println(file.getName());
                out.write(file.getBytes());
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                result =  "上传失败," + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                result =  "上传失败," + e.getMessage();
            }
            result = "上传成功";
        } else {
            result =  "上传失败，因为文件是空的.";
        }
        return result;
    }
    @RequestMapping(value = "/uploadRegressiveJar")
    @ResponseBody
    public String uploadRegressiveJar(@RequestParam("file") MultipartFile file){
        String project = file.getOriginalFilename();

        System.out.println(project);
        File projectFile = new File(baseConfig.getRegressionFilePath() + project);
//如果文件夹不存在则创建
        if (!projectFile.exists() && !projectFile.isDirectory()) {
            System.out.println("//不存在");
            projectFile.mkdir();
        } else {
            System.out.println("文件夹存在");
        }
        String result = "";
        if (!file.isEmpty()) {
            try {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(baseConfig.getRegressionFilePath()+project+"\\" + file.getOriginalFilename())));
                System.out.println(file.getName());
                out.write(file.getBytes());
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                result =  "上传失败," + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                result =  "上传失败," + e.getMessage();
            }
            result = "上传成功";
        } else {
            result =  "上传失败，因为文件是空的.";
        }
        return result;
    }
    @RequestMapping(value = "/fileList", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> getFileList(){
        File uploadedDirectory = new File(baseConfig.getUploadedFilePath());
        ArrayList filenames = new ArrayList();
        if(uploadedDirectory.isDirectory()) {
            File[] files = uploadedDirectory.listFiles();
            for (File file : files) {
                if(!file.isDirectory()) {
                    filenames.add(file.getName());
                }
            }
        }
        HashMap<String, Object> result = new HashMap<>();
        result.put("result", filenames);
        return result;

    }



}
