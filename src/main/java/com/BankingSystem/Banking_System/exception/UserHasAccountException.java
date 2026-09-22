package com.BankingSystem.Banking_System.exception;

public class UserHasAccountException extends RuntimeException {
    public UserHasAccountException(String message) {
        super(message);
    }
}
