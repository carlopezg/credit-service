package com.carlopezg.creditservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class CreditLineResponseDto {

    private double requestedCreditLine;
    private double recommendedCreditLine;

    public boolean isAuthorized() {
        return this.recommendedCreditLine > this.requestedCreditLine;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Double getAuthorizedCreditLine() {
        return isAuthorized() ? this.recommendedCreditLine : null;
    }
}
