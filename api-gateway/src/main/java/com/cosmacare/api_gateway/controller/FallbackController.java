package com.cosmacare.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    /**
     * Generic fallback that detects the service name from the path variable
     * Example URL: /fallback/repair
     */
    @GetMapping("/{serviceName}")
    public ResponseEntity<Map<String, Object>> fallback(@PathVariable String serviceName) {
        String message = String.format("%s service is temporarily unavailable. Please try again shortly.", capitalize(serviceName));
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "message", message
                ));
    }

    // Optional utility to capitalize first letter
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
