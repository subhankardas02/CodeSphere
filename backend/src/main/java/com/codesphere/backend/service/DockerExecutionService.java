package com.codesphere.backend.service;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;

@Service 
public class DockerExecutionService {

    public Path createTempDirectory() throws IOException{
        Path tempPath=Files.createTempDirectory("codesphere-");
        
        return tempPath;
    }

    public void saveSourceCode(Path dirPath, String content) throws IOException{
        Path tempFile=dirPath.resolve("main.cpp");
        Files.writeString(tempFile, content);

    }
    public void testSourceCodeSaving() throws IOException{
        Path dirPath=createTempDirectory();
        String content="#include<iostream> using namespace std; int main(){cout<<\"hello world\"<<endl;}";
        saveSourceCode(dirPath, content);
        dirPath=dirPath.resolve("main.cpp");
        boolean exists=Files.exists(dirPath);
        System.out.println("File saved successfully: " + exists);
    }
    
    

}
