package com.codesphere.backend.dto;

public class ExecutionJob {
    private Long jobId;
    private String language;
    private String code;
    private String input;
    private String status="QUEUED";
    private String leaseToken;
    

    public ExecutionJob(long jobId, String language, String code, String input) {
        this.jobId=jobId;
        this.language=language;
        this.code=code;
        this.input=input;

    }

    public Long getJobId() {
        return jobId;
    }
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getInput() {
        return input;
    }
    public void setInput(String input) {
        this.input = input;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    


    
}
