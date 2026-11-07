package com.codesphere.backend.dto;

public class ApiResponse {
    private boolean success=true;
    private String message;
    private String language;
    
    public void setSuccess(boolean success){
        this.success=success;
    }

    public void setMessage(String message){
        this.message=message;
    }

    public void setLanguage(String language){
        this.language=language;
    }

    public boolean getSuccess(){
        return this.success;
    }

    public String getMessage(){
        return this.message;
    }

    public String getLanguage(){
        return this.language;
    }

    
}
