package com.codesphere.backend.dto;


public class CodeRequest {
    private String language;
    private String code;
    
    public void setLanguage(String language) {
        this.language=language;
    }

    public void setCode(String code){
        this.code=code;
    }

    public String getLanguage(){
        return language;
    }

    public String getCode(){
        return code;
    }

}
