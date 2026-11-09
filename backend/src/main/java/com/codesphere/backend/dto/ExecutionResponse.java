package com.codesphere.backend.dto;

public class ExecutionResponse {
    private boolean success=false;
    private String output;
    private String error;
    private int exitCode;

    public void setSuccess(boolean success){
        this.success=success;
    }

    public boolean isSuccess(){
        return success;
    }

    public void setOutput(String output){
        this.output=output;
    }

    public String getOutput(){
        return output;
    }

    public void setError(String error){
        this.error=error;
    }

    public String getError(){
        return error;
    }

    public void setExitCode(int exitCode){
        this.exitCode=exitCode;
    }
    
    public int getExitCode(){
        return exitCode;
    }

}
