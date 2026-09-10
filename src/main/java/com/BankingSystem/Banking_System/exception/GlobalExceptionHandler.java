package com.BankingSystem.Banking_System.exception;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.BankingSystem.Banking_System.dto.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerNotFound(UserNotFoundException userNotFoundException){

        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setMessage(userNotFoundException.getMessage());
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setTimestamp(LocalDateTime.now());


        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

}
