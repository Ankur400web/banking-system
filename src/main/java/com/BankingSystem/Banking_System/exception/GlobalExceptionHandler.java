package com.BankingSystem.Banking_System.exception;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handlerNotFound(UserNotFoundException userNotFoundException){

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(userNotFoundException.getMessage());
    }

}
