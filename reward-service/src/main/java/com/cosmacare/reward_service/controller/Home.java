package com.cosmacare.reward_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testRewardService")
public class Home {
    @GetMapping
    public String welcome(){
        return "Cosmacare Reward Service Is Live...";
    }
}