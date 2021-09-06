package com.carlopezg.creditservice.exception;

public class RateLimitExceededException extends Exception {

    public RateLimitExceededException() {
    }

    public RateLimitExceededException(String message) {
        super(message);
    }

    public RateLimitExceededException(String message, Throwable t) {
        super(message, t);
    }
}
