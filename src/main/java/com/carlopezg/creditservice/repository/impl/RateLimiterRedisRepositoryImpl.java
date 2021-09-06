package com.carlopezg.creditservice.repository.impl;

import com.carlopezg.creditservice.domain.CustomRateLimiter;
import com.carlopezg.creditservice.repository.RateLimiterRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
public class RateLimiterRedisRepositoryImpl implements RateLimiterRedisRepository {

    private static final String REDIS_ENTITY = "rate_limiter";

    @Autowired
    private RedisTemplate<String, CustomRateLimiter> redisTemplate;

    private HashOperations<String, String, CustomRateLimiter> hashOperations;

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public CustomRateLimiter findRateLimiterById(String applicant) {
        return hashOperations.get(REDIS_ENTITY, applicant);
    }

    @Override
    public void saveRateLimiterById(String applicant, CustomRateLimiter customRateLimiter) {
        hashOperations.put(REDIS_ENTITY, applicant, customRateLimiter);
    }
}
