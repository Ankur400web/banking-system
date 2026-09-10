package com.BankingSystem.Banking_System.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class ErrorResponse {
    int status;
    String message;
    LocalDateTime timestamp;

    Map<String, String> errors;



}
