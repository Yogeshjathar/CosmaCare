package com.cosmacare.repair_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/repairs")
public class Home {
    @GetMapping({"","/"})
    public String welcome(){
        return "Cosmacare Repair Service";
    }
}
