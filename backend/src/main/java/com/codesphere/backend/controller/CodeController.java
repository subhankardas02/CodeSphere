package com.codesphere.backend.controller;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

import com.codesphere.backend.dto.ApiResponse;
import com.codesphere.backend.dto.CodeRequest;
import com.codesphere.backend.dto.ExecutionResponse;
import com.codesphere.backend.dto.JobStatusResponse;
import com.codesphere.backend.entity.Execution;
import com.codesphere.backend.repository.ExecutionRepository;
import com.codesphere.backend.service.DockerExecutionService;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class CodeController {
    private final DockerExecutionService dockerExecutionService;
    private final ExecutionRepository executionRepository;

    public CodeController(DockerExecutionService dockerExecutionService, ExecutionRepository executionRepository) {
        this.dockerExecutionService = dockerExecutionService;
        this.executionRepository = executionRepository;

    }

    @PostMapping("/api/code/submit")
    public ResponseEntity<?> submitCode(@RequestBody CodeRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        if (!"cpp".equals(request.getLanguage())) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Unsupported Language");
            apiResponse.setLanguage(request.getLanguage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
        if (request.getCode() == null || request.getCode().isBlank()) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Code cannot be empty");
            apiResponse.setLanguage(request.getLanguage());
            return ResponseEntity.badRequest().body(apiResponse);
        }

        try {
            String userInput = request.getInput() == null ? "" : request.getInput();
            Long jobId = dockerExecutionService.submitJob(request.getCode(), userInput);

            return ResponseEntity.accepted().body(Map.of("jobId", jobId, "status", "QUEUED"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    @GetMapping("/api/test")
    public ResponseEntity<ExecutionResponse> testDockerService() throws IOException, InterruptedException {
        Path tempPath = dockerExecutionService.testSourceCodeSaving();
        return ResponseEntity.ok(dockerExecutionService.testProcessBuilder(tempPath, ""));

    }

    @GetMapping("/api/code/history")
    public Page<Execution> executionHistory(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable=PageRequest.of(page, size, Sort.by("id").descending());

        return executionRepository.findAll(pageable);
    }

    @GetMapping("/api/code/status/{jobId}")
    public ResponseEntity<JobStatusResponse> getJobStatus(@PathVariable Long jobId) {
        Execution execution=executionRepository.findById(jobId).orElse(null);
        if(execution==null){
            return ResponseEntity.notFound().build();
        }
        JobStatusResponse response=new JobStatusResponse();
        response.setJobId(execution.getId());
        response.setError(execution.getError());
        response.setExitCode(execution.getExitCode());
        response.setOutput(execution.getOutput());
        response.setStatus(execution.getStatus());
        return ResponseEntity.ok(response);

    }
    

}
