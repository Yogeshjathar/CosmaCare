package com.cosmacare.eureka_server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testEurekaServer")
public class Home {
    @GetMapping
    public String welcome(){
        return "Cosmacare Eureka Server Is Live...";
    }
}