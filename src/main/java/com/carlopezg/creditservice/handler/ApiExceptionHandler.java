package com.carlopezg.creditservice.handler;

import com.carlopezg.creditservice.exception.CreditRetriesException;
import com.carlopezg.creditservice.exception.InvalidCreditLineRequestException;
import com.carlopezg.creditservice.exception.RateLimitExceededException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidCreditLineRequestException.class)
    protected ResponseEntity<ErrorMessage> handleCreditLineConflict(InvalidCreditLineRequestException ex) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessage(ex.getMessage());
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    protected ResponseEntity handleTooManyRequestsCustomException(RateLimitExceededException ex) {
        return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(CreditRetriesException.class)
    protected ResponseEntity<ErrorMessage> handleLockerCreditRequest(CreditRetriesException ex) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessage(ex.getMessage());
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessage("Invalid request");
        errorMessage.setDetails(new ArrayList<>());

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errorMessage.getDetails().add(message);
        });
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
