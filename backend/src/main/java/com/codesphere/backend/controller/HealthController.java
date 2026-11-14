package com.codesphere.backend.controller;

import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codesphere.backend.service.DockerExecutionService;

import org.springframework.web.bind.annotation.RequestParam;


@Profile("api")
@RestController 
public class HealthController {

    private String status="UP";
    private String service="CodeSphere";

    @GetMapping("/api/health") 
    public Map<String, String> getHealthInformation(){
        return Map.of("status", status, "service", service);
    }

    
}