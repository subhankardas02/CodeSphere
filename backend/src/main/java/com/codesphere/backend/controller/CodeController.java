package com.codesphere.backend.controller;
import java.nio.file.Path;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import com.codesphere.backend.dto.ApiResponse;
import com.codesphere.backend.dto.CodeRequest;
import com.codesphere.backend.dto.ExecutionResponse;
import com.codesphere.backend.entity.Execution;
import com.codesphere.backend.repository.ExecutionRepository;
import com.codesphere.backend.service.DockerExecutionService;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;




@RestController 
public class CodeController {
    private final DockerExecutionService dockerExecutionService;
    private final ExecutionRepository executionRepository;

    public CodeController(DockerExecutionService dockerExecutionService, ExecutionRepository executionRepository){
        this.dockerExecutionService=dockerExecutionService;
        this.executionRepository=executionRepository;

    }

    @PostMapping("/api/code/submit")
    public ResponseEntity<?> submitCode(@RequestBody CodeRequest request) {
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

        try {
            ExecutionResponse executionResponse=dockerExecutionService.executeCode(request.getCode());
            return ResponseEntity.ok(executionResponse);
        } catch (IOException | InterruptedException e) {
            if(e instanceof InterruptedException){
                Thread.currentThread().interrupt();
            }
            return ResponseEntity.internalServerError().build();
        }

    }
    
    @GetMapping("/api/test")
    public ResponseEntity<ExecutionResponse> testDockerService() throws IOException, InterruptedException{
        Path tempPath=dockerExecutionService.testSourceCodeSaving();
        return ResponseEntity.ok(dockerExecutionService.testProcessBuilder(tempPath));

    }

    @GetMapping("/api/code/history")
    public List<Execution> executionHistory() {

        return executionRepository.findAll();
    }
    
    
    


}
