package com.carlopezg.creditservice.exception;

public class CreditRetriesException extends Exception {

    public CreditRetriesException() {
    }

    public CreditRetriesException(String message) {
        super(message);
    }

    public CreditRetriesException(String message, Throwable t) {
        super(message, t);
    }
}
