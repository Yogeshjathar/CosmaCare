package com.cosmacare.cosmacare_user_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("")
public class Home {
    @GetMapping
    public static String welcome(){
        return "Welcome to user-service of cosma-care";
    }
}
