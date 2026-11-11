package com.codesphere.backend.dto;


public class CodeRequest {
    private String language;
    private String code;
    private String input;
    
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
    
    public void setInput(String input){
        this.input=input;
    }
    public String getInput(){
        return input;
    }


}
