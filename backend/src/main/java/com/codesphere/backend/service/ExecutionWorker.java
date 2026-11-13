package com.codesphere.backend.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.codesphere.backend.dto.ExecutionJob;
import com.codesphere.backend.dto.ExecutionResponse;
import com.codesphere.backend.entity.Execution;
import com.codesphere.backend.repository.ExecutionRepository;

@Component 
public class ExecutionWorker {
    private final RedisService redisService;
    private final DockerExecutionService dockerExecutionService;
    private final ExecutionRepository executionRepository;

    public ExecutionWorker(RedisService redisService, DockerExecutionService dockerExecutionService, ExecutionRepository executionRepository){
        this.redisService=redisService;
        this.dockerExecutionService=dockerExecutionService;
        this.executionRepository=executionRepository;
    }
    @Scheduled(fixedDelay = 1000)
    public void processNextJob() throws Exception{
        ExecutionJob job=redisService.dequeueJob();
        if(job==null){
            return;
        }
        Execution execution=executionRepository.findById(job.getJobId()).orElseThrow(()->new RuntimeException("Execution not found"));
        execution.setStatus("RUNNING");
        executionRepository.save(execution);
        ExecutionResponse response=dockerExecutionService.runCode(job.getCode(), job.getInput());
        execution.setOutput(response.getOutput());
        execution.setError(response.getError());
        execution.setExitCode(response.getExitCode());
        if(response.isSuccess()){
            execution.setStatus("SUCCESS");
        }
        else if(response.getExitCode()==-1){
            execution.setStatus("TIMEOUT");
        }
        else{
            execution.setStatus("FAILED");
        }
        executionRepository.save(execution);
        redisService.setValueExpiry("execution"+job.getJobId(), execution.getStatus(), 3600);


    }



    
}
