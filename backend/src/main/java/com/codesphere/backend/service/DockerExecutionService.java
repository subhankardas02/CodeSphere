package com.codesphere.backend.service;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    public Path testSourceCodeSaving() throws IOException{
        Path dirPath=createTempDirectory();
        String content =
    "#include <iostream>\n" +
    "using namespace std;\n" +
    "int main() {\n" +
    "    cout << \"hello world\" << endl;\n" +
    "    return 0;\n" +
    "}\n";
        saveSourceCode(dirPath, content);
        Path filePath=dirPath.resolve("main.cpp");
        boolean exists=Files.exists(filePath);
        System.out.println("File saved successfully: " + exists);
        return dirPath;
    }

    public void testProcessBuilder(Path path) throws IOException, InterruptedException{
        String volume_mount=path.toAbsolutePath().toString()+":/app";

        ProcessBuilder processBuilder=new ProcessBuilder("docker", "run", "--rm", "-v", volume_mount, "-w", "/app", "gcc:14", "bash", "-c", "g++ main.cpp -o main && ./main");
        processBuilder.redirectErrorStream(true);
        Process process=processBuilder.start();
        BufferedReader bufferReader=new BufferedReader(new InputStreamReader(process.getInputStream()));    
        String message;
        while((message=bufferReader.readLine())!=null){
            System.out.println(message);
        }
        int exitCode=process.waitFor();
        System.out.println(exitCode);
        
        
    }
    

}
