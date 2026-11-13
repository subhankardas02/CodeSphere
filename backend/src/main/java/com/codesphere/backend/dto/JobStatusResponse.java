package com.codesphere.backend.dto;

public class JobStatusResponse {
    private Long jobId;
    private String status;
    private String output;
    private String error;
    private Integer exitCode;
    
    public Long getJobId() {
        return jobId;
    }
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getOutput() {
        return output;
    }
    public void setOutput(String output) {
        this.output = output;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
    public Integer getExitCode() {
        return exitCode;
    }
    public void setExitCode(Integer exitCode) {
        this.exitCode = exitCode;
    }
    

    
}
