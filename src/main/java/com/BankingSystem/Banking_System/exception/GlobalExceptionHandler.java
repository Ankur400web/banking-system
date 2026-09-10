package com.BankingSystem.Banking_System.exception;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.BankingSystem.Banking_System.dto.ErrorResponse;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException methodArgumentNotValidException){

        Map<String, String> errors = new HashMap<>();

        for(FieldError fieldError: methodArgumentNotValidException.getBindingResult().getFieldErrors()){
            errors.put(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );

        }

        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage("Validation Failed");
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setErrors(errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

}
