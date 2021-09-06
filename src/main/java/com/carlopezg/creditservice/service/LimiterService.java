package com.carlopezg.creditservice.service;

import com.carlopezg.creditservice.exception.CreditRetriesException;

public interface LimiterService {

    boolean canMakeARetry(String applicant, long requestedDate) throws CreditRetriesException;

    boolean canMakeARequest(String applicant, long requestedDate) throws CreditRetriesException;

    void registerRejection(String applicant, long requestedDate);

    void registerRequest(String applicant, long requestedDate);

}
