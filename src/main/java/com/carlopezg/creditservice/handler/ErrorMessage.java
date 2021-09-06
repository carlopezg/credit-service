package com.carlopezg.creditservice.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorMessage {

    private String message;

    private List<String> details;
}
