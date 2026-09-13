package com.BankingSystem.Banking_System.dto;

import com.BankingSystem.Banking_System.enums.TransactionTypes;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
public class TransactionResponse {

    private Long id;
    private String accountNumber;
    private TransactionTypes type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private LocalDateTime createdAt;
}
