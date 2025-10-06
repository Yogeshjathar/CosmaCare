package com.cosmacare.notification_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testNotification")
public class Home {
    @GetMapping
    public String welcome(){
        return "Cosmacare Notification Service Is Live...";
    }
}