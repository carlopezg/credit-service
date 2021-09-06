package com.carlopezg.creditservice.repository;

import com.carlopezg.creditservice.domain.CustomRateLimiter;

public interface RateLimiterRedisRepository {

    CustomRateLimiter findRateLimiterById(String applicant);

    void saveRateLimiterById(String applicant, CustomRateLimiter customRateLimiter);
}
