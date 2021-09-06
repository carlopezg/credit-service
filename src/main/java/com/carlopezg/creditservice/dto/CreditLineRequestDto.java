package com.carlopezg.creditservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditLineRequestDto {

    @NotEmpty(message = "Founding type is necessary")
    private String foundingType;

    @NotNull(message = "Cash balance is necessary")
    private Double cashBalance;

    @NotNull(message = "Monthly revenue is necessary")
    private Double monthlyRevenue;

    @NotNull(message = "Requested credit line is necessary")
    private Double requestedCreditLine;

    @NotNull(message = "Requested date is necessary")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date requestedDate;
}
