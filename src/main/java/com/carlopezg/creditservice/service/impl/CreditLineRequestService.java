package com.carlopezg.creditservice.service.impl;

import com.carlopezg.creditservice.domain.CreditLineRequest;
import com.carlopezg.creditservice.dto.CreditLineRequestDto;
import com.carlopezg.creditservice.dto.CreditLineResponseDto;
import com.carlopezg.creditservice.enums.CreditLineRequestStatus;
import com.carlopezg.creditservice.exception.CreditRetriesException;
import com.carlopezg.creditservice.exception.InvalidCreditLineRequestException;
import com.carlopezg.creditservice.exception.RateLimitExceededException;
import com.carlopezg.creditservice.repository.CreditLineRequestRepository;
import com.carlopezg.creditservice.service.LimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class CreditLineRequestService {

    @Autowired
    CreditLineRequestRepository repository;

    @Autowired
    RiskManagementService riskManagementService;

    @Autowired
    LimiterService limiterService;

    public CreditLineResponseDto processCreditLineRequest(CreditLineRequestDto creditLineRequest, Map<String, String> headers)
            throws InvalidCreditLineRequestException, RateLimitExceededException, CreditRetriesException {
        String applicant = getApplicant(headers);
        Optional<CreditLineRequest> previousRequest = repository.findFirstByApplicantOrderByRequestedDateDesc(applicant);

        if (previousRequest.isPresent()) {
            if (previousRequest.get().getStatus().equals(CreditLineRequestStatus.ACCEPTED)) {
                if (limiterService.canMakeARequest(applicant, creditLineRequest.getRequestedDate().getTime())) {
                    limiterService.registerRequest(applicant, creditLineRequest.getRequestedDate().getTime());
                    return new CreditLineResponseDto(previousRequest.get().getRequestedCreditLine(), previousRequest.get().getRecommendedCreditLine());
                } else {
                    throw new RateLimitExceededException("Rate limit exceeded for accepted requests");
                }
            } else {
                if (limiterService.canMakeARetry(applicant, creditLineRequest.getRequestedDate().getTime())) {
                    return doProcess(applicant, creditLineRequest);
                } else {
                    throw new RateLimitExceededException("Rate limit exceeded for rejected requests");
                }
            }
        } else {
            return doProcess(applicant, creditLineRequest);
        }
    }

    private String getApplicant(Map<String, String> headers) {
        if (headers.get("x-application-key") != null)
            return headers.get("x-application-key");
        else if (headers.get("x-user-id") != null)
            return headers.get("x-user-id");
        else
            return headers.get("x-forwarded-for");
    }

    private CreditLineResponseDto doProcess(String applicant, CreditLineRequestDto creditLineRequest) throws InvalidCreditLineRequestException {
        CreditLineResponseDto response = new CreditLineResponseDto(creditLineRequest.getRequestedCreditLine(), riskManagementService.getRecommendedCreditLine(creditLineRequest));
        if (response.isAuthorized())
            limiterService.registerRequest(applicant, creditLineRequest.getRequestedDate().getTime());
        else
            limiterService.registerRejection(applicant, creditLineRequest.getRequestedDate().getTime());

        saveCreditLineRequest(applicant, creditLineRequest.getRequestedCreditLine(), creditLineRequest.getRequestedDate(), response.getAuthorizedCreditLine(), response.isAuthorized());
        return response;
    }

    public void saveCreditLineRequest(String applicant, Double requestedCreditLine, Date requestedDate, Double recommendedCreditLine, Boolean isAuthorized) {
        CreditLineRequest clr = new CreditLineRequest();
        clr.setApplicant(applicant);
        clr.setRequestedCreditLine(requestedCreditLine);
        clr.setRequestedDate(requestedDate);
        clr.setStatus(isAuthorized ? CreditLineRequestStatus.ACCEPTED : CreditLineRequestStatus.REJECTED);
        clr.setRecommendedCreditLine(recommendedCreditLine);
        repository.save(clr);
    }
}
