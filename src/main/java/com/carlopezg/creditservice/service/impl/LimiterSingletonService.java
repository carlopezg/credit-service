package com.carlopezg.creditservice.service.impl;

import com.carlopezg.creditservice.config.RuleServiceConfig;
import com.carlopezg.creditservice.domain.CustomRateLimiter;
import com.carlopezg.creditservice.exception.CreditRetriesException;
import com.carlopezg.creditservice.service.LimiterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope("singleton")
@Slf4j
@Profile("without-redis")
public class LimiterSingletonService implements LimiterService {

    private final Map<String, CustomRateLimiter> rateLimiter = new HashMap<>();

    @Autowired
    RuleServiceConfig ruleServiceConfig;

    @Override
    public boolean canMakeARetry(String applicant, long requestedDate) throws CreditRetriesException {
        CustomRateLimiter crl = rateLimiter.get(applicant);
        if (crl != null) {
            if (crl.getRejectionAttempts() >= ruleServiceConfig.getToleratedRejections())
                throw new CreditRetriesException("A sales agent will contact you");

            return crl.getLastRejection() < requestedDate - ruleServiceConfig.getRetryInterval();
        }
        return true;
    }

    @Override
    public boolean canMakeARequest(String applicant, long requestedDate) throws CreditRetriesException {
        CustomRateLimiter crl = rateLimiter.get(applicant);
        if (crl != null) {
            if (crl.getRejectionAttempts() >= ruleServiceConfig.getToleratedRejections())
                throw new CreditRetriesException("A sales agent will contact you");

            return crl.getAllowedRequest().stream()
                    .filter(ar -> ar > requestedDate - ruleServiceConfig.getMaxAcceptedRequestsInterval())
                    .count() < ruleServiceConfig.getMaxAcceptedRequests();
        }
        return true;
    }

    @Override
    public void registerRejection(String applicant, long requestedDate) {
        CustomRateLimiter crl = rateLimiter.get(applicant);
        if (crl != null) {
            crl.setLastRejection(requestedDate);
            crl.setRejectionAttempts(crl.getRejectionAttempts() + 1);
        } else {
            crl = new CustomRateLimiter();
            crl.setRejectionAttempts(1);
            crl.setLastRejection(requestedDate);
            rateLimiter.put(applicant, crl);
        }
    }

    @Override
    public void registerRequest(String applicant, long requestedDate) {
        CustomRateLimiter crl = rateLimiter.get(applicant);
        if (crl != null) {
            if (crl.getAllowedRequest().size() < ruleServiceConfig.getMaxAcceptedRequests())
                crl.getAllowedRequest().add(requestedDate);
            else {
                List<Long> currentAllowedRequest = rateLimiter.get(applicant).getAllowedRequest().stream().filter(ar -> ar > requestedDate - ruleServiceConfig.getMaxAcceptedRequestsInterval()).collect(Collectors.toList());
                currentAllowedRequest.add(requestedDate);

                crl.setAllowedRequest(currentAllowedRequest);
            }
        } else {
            crl = new CustomRateLimiter();
            crl.setAllowedRequest(new ArrayList<>(Arrays.asList(requestedDate)));
            rateLimiter.put(applicant, crl);
        }
    }

}
