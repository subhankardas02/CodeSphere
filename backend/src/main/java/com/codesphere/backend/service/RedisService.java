package com.codesphere.backend.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.codesphere.backend.dto.ExecutionJob;
import tools.jackson.databind.ObjectMapper;

@Service 
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper){
        this.redisTemplate=redisTemplate;
        this.objectMapper=objectMapper;
    }

    public void setValue(String key, String value){
        redisTemplate.opsForValue().set(key, value);
    }

    public String getValue(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteValue(String key){
        redisTemplate.delete(key);
    }

    public void setValueExpiry(String key, String value, long second){
        redisTemplate.opsForValue().set(key, value, java.time.Duration.ofSeconds(second));
    }
    
    public void enqueueJob(ExecutionJob job){
        String jobJson=objectMapper.writeValueAsString(job);
        redisTemplate.opsForList().leftPush("codesphere:jobs", jobJson);
    }

    public ExecutionJob dequeueJob() throws Exception{
        String jobJson=redisTemplate.opsForList().rightPop("codesphere:jobs");

        if(jobJson==null){
            return null;
        }

        return objectMapper.readValue(jobJson, ExecutionJob.class);
    }


}
