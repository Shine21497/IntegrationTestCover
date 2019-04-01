package com.shine.integrationtestcover.controller;

import com.shine.integrationtestcover.config.BaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Author: Shine
 * @Date: 2019/3/31
 */
@RestController
public class TestUploadController {
    @Autowired
    private BaseConfig baseConfig;

    @RequestMapping(value = "/uploadTestCase")
    @ResponseBody
    public String uploadTestCase(@RequestParam("file") MultipartFile testCase, @RequestParam("selectedProject")String selectedProject){
        if (!testCase.isEmpty()) {
            try {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(baseConfig.getUploadedTestPath(selectedProject) + testCase.getOriginalFilename())));
                out.write(testCase.getBytes());
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

    @RequestMapping(value = "/testCaseList", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> getTestCaseList(){
        HashMap<String, HashMap<String, ArrayList<String>>> projectToTestFiles = new HashMap<>();
        File uploadedProjectDirectory = new File(baseConfig.getUploadedTestPath(""));
        if(uploadedProjectDirectory.isDirectory()) {
            File[] projectDirectorys = uploadedProjectDirectory.listFiles();
            for (File projectDirectory : projectDirectorys) {
                if(projectDirectory.isDirectory()) {
                    projectToTestFiles.put(projectDirectory.getName(), new HashMap<>());
                    File[] testFiles = projectDirectory.listFiles();
                    for(File testFile : testFiles) {
                        ArrayList<String> methods = new ArrayList<>();
                        //wait for compile file and get test methods
                        methods.add("allMehtods");
                        projectToTestFiles.get(projectDirectory.getName()).put(testFile.getName(), methods);

                    }
                    projectToTestFiles.get(projectDirectory.getName()).put("allTestFiles", new ArrayList<>());
                }
            }
        }
        HashMap<String, Object> result = new HashMap<>();
        result.put("result", projectToTestFiles);
        return result;

    }
}
