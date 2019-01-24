package com.jie.service.controller;

import com.jie.service.iservice.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestControl {
    @Autowired
    private ITestService iTestService;
    @GetMapping("/test")
    public String test(){
        return iTestService.test();
    }
}
