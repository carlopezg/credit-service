package com.carlopezg.creditservice.controller;

import com.carlopezg.creditservice.dto.CreditLineRequestDto;
import com.carlopezg.creditservice.exception.CreditRetriesException;
import com.carlopezg.creditservice.exception.InvalidCreditLineRequestException;
import com.carlopezg.creditservice.exception.RateLimitExceededException;
import com.carlopezg.creditservice.repository.RateLimiterRedisRepository;
import com.carlopezg.creditservice.service.impl.CreditLineRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@Slf4j
public class CreditController {

    @Autowired
    CreditLineRequestService creditLineRequestService;

    @Autowired
    RateLimiterRedisRepository rateLimiterRedisRepository;

    @PostMapping("/credit-line")
    public ResponseEntity<?> processCreditLineRequest(@Valid @RequestBody CreditLineRequestDto creditLineRequest,
                                                      @RequestHeader Map<String, String> headers)
            throws InvalidCreditLineRequestException, RateLimitExceededException, CreditRetriesException {
        try {
            return ResponseEntity.ok(creditLineRequestService.processCreditLineRequest(creditLineRequest, headers));
        } catch (RateLimitExceededException ex) {
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        }
    }
}
