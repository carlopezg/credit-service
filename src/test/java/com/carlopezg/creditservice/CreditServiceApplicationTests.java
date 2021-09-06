package com.carlopezg.creditservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class CreditServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectmapper;

    @Test
    public void testCreditApproved() throws Exception {
        Map<String, Object> request = randomRequest("SME", true);
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-application-key", UUID.randomUUID().toString());

        String response = doPost(request, headers)
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.authorized", is(true)))
                .andReturn().getResponse().getContentAsString();

        System.out.println("credit approved response: " + response);
    }

    @Test
    public void testCreditRejection() throws Exception {
        Map<String, Object> request = randomRequest("Startup", false);
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-application-key", UUID.randomUUID().toString());

        String response = doPost(request, headers)
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.authorized", is(false)))
                .andReturn().getResponse().getContentAsString();

        System.out.println("credit rejected response: " + response);
    }

    @Test
    public void testApprovedRateLimit() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-application-key", UUID.randomUUID().toString());

        int i = 1;
        for (; i <= 3; i++) {
            String response = doPost(randomRequest("Startup", true), headers)
                    .andExpect(status().is(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.authorized", is(true)))
                    .andReturn().getResponse().getContentAsString();
            System.out.println(i + "° credit request approved response: " + response);
            Thread.sleep(39_000);
        }

        int responseStatus = doPost(randomRequest("Startup", true), headers)
                .andExpect(status().is(HttpStatus.TOO_MANY_REQUESTS.value()))
                .andReturn().getResponse().getStatus();
        System.out.println(i + "° approved credit request with response status: " + responseStatus);
    }

    @Test
    public void testRejectRateLimit() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-application-key", UUID.randomUUID().toString());

        String response = doPost(randomRequest("SME", false), headers)
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.authorized", is(false)))
                .andReturn().getResponse().getContentAsString();
        System.out.println("1° credit request rejected response: " + response);
        Thread.sleep(29_000);

        int error = doPost(randomRequest("SME", false), headers)
                .andExpect(status().is(HttpStatus.TOO_MANY_REQUESTS.value()))
                .andReturn().getResponse().getStatus();
        System.out.println("2° rejected credit request after 18 seconds return status code: " + error);
    }

    @Test
    public void testLockRejectionRequest() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-application-key", UUID.randomUUID().toString());

        int i = 1;
        for (; i <= 3; i++) {
            String response = doPost(randomRequest("Startup", false), headers)
                    .andExpect(status().is(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.authorized", is(false)))
                    .andReturn().getResponse().getContentAsString();
            System.out.println(i + "° credit request rejected response: " + response);
            Thread.sleep(30_000);
        }

        String response = doPost(randomRequest("Startup", false), headers)
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andReturn().getResponse().getContentAsString();
        System.out.println("4° credit request with response: " + response);
    }

    private ResultActions doPost(Map<String, Object> request, HttpHeaders headers) throws Exception {
        return mockMvc.perform(
                post("/credit-line")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectmapper.writeValueAsString(request)));
    }

    private Map<String, Object> randomRequest(String foundingType, boolean toBeApproved) {
        Map<String, Object> request = new HashMap<>();
        request.put("foundingType", foundingType);
        request.put("requestedDate", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date()));
        request.put("cashBalance", new Random().doubles(500, 1000).findFirst().getAsDouble());
        request.put("monthlyRevenue", new Random().doubles(4000, 5000).findFirst().getAsDouble());
        request.put("requestedCreditLine", randomizeRequestCreditLine(foundingType, request, toBeApproved));
        return request;
    }

    private double randomizeRequestCreditLine(String foundingType, Map<String, Object> requestParameters, boolean toBeApproved) {
        double recommendedCreditLine = getRecommendedCreditLine(foundingType, requestParameters);
        if (toBeApproved) {
            return new Random().doubles(recommendedCreditLine * 0.5, recommendedCreditLine - 1).findFirst().getAsDouble();
        } else {
            return new Random().doubles(recommendedCreditLine, recommendedCreditLine * 1.5).findFirst().getAsDouble();
        }
    }

    private double getRecommendedCreditLine(String foundingType, Map<String, Object> requestParameters) {
        double monthlyRevenue = Double.parseDouble(requestParameters.get("monthlyRevenue").toString());
        if (foundingType.equalsIgnoreCase("SME")) {
            return (monthlyRevenue * 0.2);
        } else {
            double cashBalance = Double.parseDouble(requestParameters.get("cashBalance").toString());
            return Math.max(monthlyRevenue * 0.2, cashBalance * 0.33);
        }
    }

}
