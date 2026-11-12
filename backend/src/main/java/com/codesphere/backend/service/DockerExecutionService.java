package com.codesphere.backend.service;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.codesphere.backend.dto.ExecutionResponse;
import com.codesphere.backend.repository.ExecutionRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import com.codesphere.backend.entity.Execution;

@Service 
public class DockerExecutionService {
    private final ExecutionRepository executionRepository;
    private final RedisService redisService;

    public DockerExecutionService(ExecutionRepository executionRepository, RedisService redisService){
        this.executionRepository=executionRepository;
        this.redisService=redisService;
    }

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

    public ExecutionResponse testProcessBuilder(Path path, String input) throws IOException, InterruptedException{
        String volume_mount=path.toAbsolutePath().toString()+":/app";
        
        ProcessBuilder processBuilder=new ProcessBuilder("docker", "run", "-i", "--rm", "-v", volume_mount, "-w", "/app", "gcc:14", "bash", "-c", "g++ main.cpp -o main && ./main");
        
        Process process=processBuilder.start();
        BufferedReader bufferReader=new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader=new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder tempOutput=new StringBuilder();
        StringBuilder tempError=new StringBuilder();
        Thread thread1=new Thread(()->{
            try{
                String message;
                while ((message = bufferReader.readLine()) != null) {
                    tempOutput.append(message).append("\n");
                }
            } 
            catch(IOException e) {
                e.printStackTrace();
            }
        });
        Thread thread2=new Thread(()->{
            try{
                String message;
                while((message=errorReader.readLine())!=null){
                    tempError.append(message).append("\n");
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        });
        thread1.start();
        thread2.start();
        process.getOutputStream().write(input.getBytes(StandardCharsets.UTF_8));
        process.getOutputStream().close();
        boolean completed = process.waitFor(5, TimeUnit.SECONDS);
            
        int exitCode;

        if(completed) {
            exitCode = process.exitValue();
        } 
        else{
            process.destroyForcibly();
            process.waitFor();
            exitCode = -1;
        }
        thread1.join();
        thread2.join();
        System.out.println(exitCode);
        ExecutionResponse executionResponse=new ExecutionResponse();
        executionResponse.setExitCode(exitCode);
        executionResponse.setOutput(tempOutput.toString());
        executionResponse.setError(tempError.toString());
        if (completed) {
            executionResponse.setSuccess(exitCode == 0);
        } 
        else {
            executionResponse.setSuccess(false);
            executionResponse.setError("Execution timed out after 5 seconds.");
        }
        bufferReader.close();
        errorReader.close();
        return executionResponse;
    }

    public ExecutionResponse executeCode(String code, String input) throws IOException, InterruptedException{
        Path dirPath=createTempDirectory();
        saveSourceCode(dirPath, code);
        ExecutionResponse executionResponse=testProcessBuilder(dirPath, input);
        Execution execution=new Execution();
        execution.setLanguage("cpp");
        execution.setSourceCode(code);
        execution.setOutput(executionResponse.getOutput());
        execution.setError(executionResponse.getError());
        execution.setExitCode(executionResponse.getExitCode());
        if(executionResponse.isSuccess()){
            execution.setStatus("SUCCESS");
        }
        else if(executionResponse.getExitCode()==-1){
            execution.setStatus("TIMEOUT");
        }
        else{
            execution.setStatus("FAILED");
        }
        
        Execution savExecution=executionRepository.save(execution);
        redisService.setValueExpiry("execution:"+savExecution.getId(), savExecution.getStatus(), 3600);


        return executionResponse;
    }
        
}
