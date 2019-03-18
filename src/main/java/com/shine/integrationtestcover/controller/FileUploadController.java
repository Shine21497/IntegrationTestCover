package com.shine.integrationtestcover.controller;

import com.shine.integrationtestcover.config.BaseConfig;
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
        if (!file.isEmpty()) {
            try {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(baseConfig.getUploadedFilePath() + file.getOriginalFilename())));
                System.out.println(file.getName());
                out.write(file.getBytes());
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "上传失败," + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "上传失败," + e.getMessage();
            }
            return "上传成功";
        } else {
            return "上传失败，因为文件是空的.";
        }

    }

    @RequestMapping(value = "/fileList", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> getFileList(){
        File uploadedDirectory = new File(baseConfig.getUploadedFilePath());
        ArrayList filenames = new ArrayList();
        if(uploadedDirectory.isDirectory()) {
            File[] files = uploadedDirectory.listFiles();
            for (File file : files) {
                filenames.add(file.getName());
            }
        }
        HashMap<String, Object> result = new HashMap<>();
        result.put("result", filenames);
        return result;

    }



}
