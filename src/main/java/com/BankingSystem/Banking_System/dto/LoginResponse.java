package com.BankingSystem.Banking_System.dto;

import lombok.Getter;
import lombok.Setter;
import org.flywaydb.core.internal.parser.TokenType;


@Getter
@Setter
public class LoginResponse {

    private String token;

    private String  tokenType;
}
