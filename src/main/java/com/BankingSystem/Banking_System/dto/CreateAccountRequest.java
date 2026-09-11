package com.BankingSystem.Banking_System.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String accountType;
}
