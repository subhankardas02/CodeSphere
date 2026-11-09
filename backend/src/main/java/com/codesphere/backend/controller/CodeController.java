package com.codesphere.backend.controller;
import java.nio.file.Path;
import org.springframework.web.bind.annotation.RestController;

import com.codesphere.backend.dto.ApiResponse;
import com.codesphere.backend.dto.CodeRequest;
import com.codesphere.backend.dto.ExecutionResponse;
import com.codesphere.backend.service.DockerExecutionService;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController 
public class CodeController {
    private final DockerExecutionService dockerExecutionService;

    public CodeController(DockerExecutionService dockerExecutionService){
        this.dockerExecutionService=dockerExecutionService;

    }

    @PostMapping("/api/code/submit")
    public ResponseEntity<ApiResponse> submitCode(@RequestBody CodeRequest request) {
        ApiResponse apiResponse=new ApiResponse();
        if(!"cpp".equals(request.getLanguage())){
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Unsupported Language");
            apiResponse.setLanguage(request.getLanguage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
        if(request.getCode()==null || request.getCode().isBlank()){
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Code cannot be empty");
            apiResponse.setLanguage(request.getLanguage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Code Received Successfully");
            apiResponse.setLanguage(request.getLanguage());
            return ResponseEntity.ok().body(apiResponse);
    }
    
    @GetMapping("/api/test")
    public ResponseEntity<ExecutionResponse> testDockerService() throws IOException, InterruptedException{
        Path tempPath=dockerExecutionService.testSourceCodeSaving();
        return ResponseEntity.ok(dockerExecutionService.testProcessBuilder(tempPath));

    }
    
    


}
