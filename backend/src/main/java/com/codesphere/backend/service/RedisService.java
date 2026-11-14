package com.codesphere.backend.service;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import com.codesphere.backend.dto.ExecutionJob;
import tools.jackson.databind.ObjectMapper;
import java.util.UUID;

@Service
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final long LEASE_DURATION_MS = 30_000;
    private static final String RECOVER_EXPIRED_JOB_SCRIPT = """
            local jobId = ARGV[1]
            local currentTime = tonumber(ARGV[2])

            local expiry = redis.call('ZSCORE', KEYS[1], jobId)

            if not expiry or tonumber(expiry) > currentTime then
                return 0
            end

            local jobs = redis.call('LRANGE', KEYS[2], 0, -1)

            for _, jobJson in ipairs(jobs) do
                local job = cjson.decode(jobJson)

                if tostring(job.jobId) == jobId then
                    redis.call('LREM', KEYS[2], 1, jobJson)
                    redis.call('LPUSH', KEYS[3], jobJson)
                    redis.call('ZREM', KEYS[1], jobId)

                    return 1
                end
            end

            return 0
            """;

    public RedisService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }

    public void setValueExpiry(String key, String value, long second) {
        redisTemplate.opsForValue().set(key, value, java.time.Duration.ofSeconds(second));
    }

    public void enqueueJob(ExecutionJob job) {
        String jobJson = objectMapper.writeValueAsString(job);
        redisTemplate.opsForList().leftPush("codesphere:jobs", jobJson);
    }

    public ExecutionJob dequeueJob() throws Exception {
        String jobJson = redisTemplate.opsForList().rightPopAndLeftPush("codesphere:jobs", "codesphere:processing");

        if (jobJson == null) {
            return null;
        }
        ExecutionJob job = objectMapper.readValue(jobJson, ExecutionJob.class);
        addJobLease(job.getJobId());
        return job;
    }

    public void acknowledgeJob(ExecutionJob job) {
        String jobJson = objectMapper.writeValueAsString(job);
        redisTemplate.opsForList().remove("codesphere:processing", 1, jobJson);
        redisTemplate.opsForZSet().remove("codesphere:job-leases", job.getJobId().toString());
    }

    public void addJobLease(Long jobId) {
        long currentTime = System.currentTimeMillis();
        long expiryTime = currentTime + LEASE_DURATION_MS;
        redisTemplate.opsForZSet().add("codesphere:job-leases", jobId.toString(), expiryTime);

    }

    public void renewJobLease(Long jobId) {
        long expiryTime = System.currentTimeMillis() + LEASE_DURATION_MS;
        redisTemplate.opsForZSet().add("codesphere:job-leases", jobId.toString(), expiryTime);

    }

    public Set<String> getExpiredJobIds() {
        return redisTemplate.opsForZSet().rangeByScore("codesphere:job-leases", 0, System.currentTimeMillis());
    }

    public Long recoverExpiredJob(Long jobId) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(RECOVER_EXPIRED_JOB_SCRIPT);
        script.setResultType(Long.class);

        return redisTemplate.execute(
                script,
                java.util.List.of(
                        "codesphere:job-leases",
                        "codesphere:processing",
                        "codesphere:jobs"),
                jobId.toString(),
                String.valueOf(System.currentTimeMillis()));

    }

}
