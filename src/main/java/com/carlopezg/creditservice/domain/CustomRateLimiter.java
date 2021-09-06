package com.carlopezg.creditservice.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@RedisHash("CustomRateLimiter")
public class CustomRateLimiter implements Serializable {

    private static final long serialVersionUID = -7591335768409589263L;

    private List<Long> allowedRequest;

    private int rejectionAttempts;

    private Long lastRejection;

    @Override
    public String toString() {
        return "{'rejectionAttempts': " + this.rejectionAttempts + ", 'lastRejection': " + this.lastRejection + "}";
    }
}
