package com.carlopezg.creditservice.service.impl;

import com.carlopezg.creditservice.config.RuleServiceConfig;
import com.carlopezg.creditservice.domain.CustomRateLimiter;
import com.carlopezg.creditservice.exception.CreditRetriesException;
import com.carlopezg.creditservice.repository.RateLimiterRedisRepository;
import com.carlopezg.creditservice.service.LimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Profile("!without-redis")
public class LimiterRedisService implements LimiterService {

    @Autowired
    RateLimiterRedisRepository repository;

    @Autowired
    RuleServiceConfig ruleServiceConfig;

    @Override
    public boolean canMakeARetry(String applicant, long requestedDate) throws CreditRetriesException {
        CustomRateLimiter crl = repository.findRateLimiterById(applicant);
        if (crl != null) {
            if (crl.getRejectionAttempts() >= ruleServiceConfig.getToleratedRejections())
                throw new CreditRetriesException("A sales agent will contact you");

            return crl.getLastRejection() < requestedDate - ruleServiceConfig.getRetryInterval();
        }
        return true;
    }

    @Override
    public boolean canMakeARequest(String applicant, long requestedDate) throws CreditRetriesException {
        CustomRateLimiter crl = repository.findRateLimiterById(applicant);
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
        CustomRateLimiter crl = repository.findRateLimiterById(applicant);
        if (crl != null) {
            crl.setLastRejection(requestedDate);
            crl.setRejectionAttempts(crl.getRejectionAttempts() + 1);
        } else {
            crl = new CustomRateLimiter();
            crl.setRejectionAttempts(1);
            crl.setLastRejection(requestedDate);
        }
        repository.saveRateLimiterById(applicant, crl);
    }

    @Override
    public void registerRequest(String applicant, long requestedDate) {
        CustomRateLimiter crl = repository.findRateLimiterById(applicant);
        if (crl != null) {
            if (crl.getAllowedRequest().size() < ruleServiceConfig.getMaxAcceptedRequests())
                crl.getAllowedRequest().add(requestedDate);
            else {
                List<Long> currentAllowedRequest = crl.getAllowedRequest().stream()
                        .filter(ar -> ar > requestedDate - ruleServiceConfig.getMaxAcceptedRequestsInterval())
                        .collect(Collectors.toList());
                currentAllowedRequest.add(requestedDate);

                crl.setAllowedRequest(currentAllowedRequest);
            }
        } else {
            crl = new CustomRateLimiter();
            crl.setAllowedRequest(new ArrayList<>(Arrays.asList(requestedDate)));
        }

        repository.saveRateLimiterById(applicant, crl);
    }
}
