package com.carlopezg.creditservice.exception;

public class InvalidCreditLineRequestException extends Exception {

    public InvalidCreditLineRequestException() {
    }

    public InvalidCreditLineRequestException(String message) {
        super(message);
    }

    public InvalidCreditLineRequestException(String message, Throwable t) {
        super(message, t);
    }
}
