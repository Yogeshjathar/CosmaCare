package com.cosmacare.api_gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testGateway")
public class Home {
    @GetMapping
    public String welcome(){
        return "Cosmacare Api Gateway Is Live...";
    }
}