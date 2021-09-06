package com.carlopezg.creditservice.service.impl;

import com.carlopezg.creditservice.config.RuleServiceConfig;
import com.carlopezg.creditservice.dto.CreditLineRequestDto;
import com.carlopezg.creditservice.exception.InvalidCreditLineRequestException;
import com.carlopezg.creditservice.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RiskManagementService {

    @Autowired
    RuleServiceConfig ruleServiceConfig;

    public double getRecommendedCreditLine(CreditLineRequestDto creditLineRequest) throws InvalidCreditLineRequestException {
        double creditLineBasedOnMonthlyRevenue = creditLineRequest.getMonthlyRevenue() * ruleServiceConfig.getMonthlyRevenueRatio();

        if (creditLineRequest.getFoundingType().equalsIgnoreCase(Constants.SME)) {
            return creditLineBasedOnMonthlyRevenue;
        } else if (creditLineRequest.getFoundingType().equalsIgnoreCase(Constants.STARTUP)) {
            double creditLineBasedOnCashBalance = creditLineRequest.getCashBalance() * ruleServiceConfig.getCashBalanceRatio();
            return Math.max(creditLineBasedOnMonthlyRevenue, creditLineBasedOnCashBalance);
        } else {
            throw new InvalidCreditLineRequestException("");
        }
    }
}
