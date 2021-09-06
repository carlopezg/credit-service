package com.carlopezg.creditservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "rules")
@Getter
@Setter
public class RuleServiceConfig {

    private double cashBalanceRatio;
    private double monthlyRevenueRatio;
    private long retryInterval;
    private int toleratedRejections;
    private int maxAcceptedRequests;
    private long maxAcceptedRequestsInterval;

}
