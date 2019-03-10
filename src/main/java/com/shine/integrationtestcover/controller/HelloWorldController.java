package com.shine.integrationtestcover.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Shine
 * @Date: 2019/3/9
 */

@RestController
public class HelloWorldController {
    @RequestMapping(value = "/helloworld", method = RequestMethod.GET)
    @ResponseBody
    public String hello() {
        System.out.println("request");
        return "hello";
    }
}
