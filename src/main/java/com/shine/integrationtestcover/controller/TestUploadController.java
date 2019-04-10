package com.shine.integrationtestcover.controller;

import com.shine.integrationtestcover.config.BaseConfig;
import com.shine.integrationtestcover.service.RunTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @Author: Shine
 * @Date: 2019/3/31
 */
@RestController
public class TestUploadController {
    @Autowired
    private BaseConfig baseConfig;

    @Autowired
    RunTestService runTestService;

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
            if(testCase.getName().contains(".zip")){
                File srcFile=new File(baseConfig.getUploadedTestPath(selectedProject) + testCase.getOriginalFilename());
                if(!srcFile.exists()){
                    throw new RuntimeException(srcFile.getPath()+"所指文件不存在");
                }
                ZipFile zipFile=null;
                try{
                    zipFile =new ZipFile(srcFile);
                    Enumeration<?> entries= zipFile.entries();
                    while(entries.hasMoreElements()){
                        ZipEntry entry=(ZipEntry)entries.nextElement();
                        if(entry.isDirectory()){
                            String dirPath=baseConfig.getUploadedTestPath(selectedProject)+"/"+entry.getName();
                            File dir =new File(dirPath);
                            dir.mkdirs();
                        }
                        else
                        {
                            File targetFile=new File(baseConfig.getUploadedTestPath(selectedProject)+"/"+entry.getName());
                            if(!targetFile.getParentFile().exists()){
                                targetFile.getParentFile().mkdirs();
                            }
                            targetFile.createNewFile();
                            InputStream inputStream=zipFile.getInputStream(entry);
                            FileOutputStream fileOutputStream=new FileOutputStream(targetFile);
                            int len;
                            byte[] buf=new byte[4096];

                            while((len= inputStream.read(buf))!=-1){
                                fileOutputStream.write(buf,0,len);
                            }
                            fileOutputStream.close();
                            inputStream.close();
                        }
                    }

                } catch (Exception e) {
                    throw new RuntimeException("unzip error from ZipUtils", e);
                }
                finally {
                    if(zipFile!=null){
                        try{
                            zipFile.close();
                        }
                        catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
            return "上传成功";
        } else {
            return "上传失败，因为文件是空的.";
        }
    }

    @RequestMapping(value = "/testCaseList", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> getTestCaseList(){
        HashMap<String, HashMap<String, List<String>>> projectToTestFiles = new HashMap<>();
        File uploadedProjectDirectory = new File(baseConfig.getUploadedTestPath(""));
        if(uploadedProjectDirectory.isDirectory()) {
            File[] projectDirectorys = uploadedProjectDirectory.listFiles();
            for (File projectDirectory : projectDirectorys) {
                if(projectDirectory.isDirectory()) {
                    projectToTestFiles.put(projectDirectory.getName(), new HashMap<>());
                    File[] testFiles = projectDirectory.listFiles();
                    for(File testFile : testFiles) {
                        List<String> methods = new ArrayList<>();
                        //wait for compile file and get test methods
                        runTestService.initate(projectDirectory.getName());
                        methods = runTestService.getMethods(testFile.getName().replace(".java", ""));
                        System.out.println(methods);
                        methods.add("allMethods");
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
