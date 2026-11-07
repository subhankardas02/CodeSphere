package com.codesphere.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.codesphere.backend.dto.CodeRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
public class CodeController {

    @PostMapping("/api/code/submit")
    public ResponseEntity<String> submitCode(@RequestBody CodeRequest request) {
        if(request.getLanguage()==null || !request.getLanguage().equals("cpp")){
            return ResponseEntity.badRequest().body("Unsupported language");
        }
        if(request.getCode()==null || request.getCode().isBlank()){
            return ResponseEntity.badRequest().body("Code cannot be empty");
        }
        return ResponseEntity.ok("Code received successfully");
        
    }
    
    


}
