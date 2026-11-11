package com.codesphere.backend.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController 
public class RedisTestController {
    
    private final StringRedisTemplate redisTemplate;

    public RedisTestController(StringRedisTemplate redisTemplate){
        this.redisTemplate=redisTemplate;
    }

    @GetMapping("/api/redis/test")
    public String testRedis(){
        redisTemplate.opsForValue().set("name", "subhankar");
        String val=redisTemplate.opsForValue().get("name");
        return val;

    }

}
