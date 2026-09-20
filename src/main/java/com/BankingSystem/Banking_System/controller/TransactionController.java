package com.BankingSystem.Banking_System.controller;

import com.BankingSystem.Banking_System.dto.DepositRequest;
import com.BankingSystem.Banking_System.dto.TransactionResponse;
import com.BankingSystem.Banking_System.dto.TransferRequest;
import com.BankingSystem.Banking_System.dto.WithdrawRequest;
import com.BankingSystem.Banking_System.service.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
@Tag(
        name = "Transactions",
        description = "Deposits, withdrawals, transfers and transaction history"
)
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public TransactionResponse deposit(@Valid @RequestBody DepositRequest depositRequest){
        return transactionService.deposit(depositRequest);
    }

    @PostMapping("/withdraw")
    public TransactionResponse withdraw(@Valid @RequestBody WithdrawRequest request){
        return transactionService.withDraw(request);
    }

    @GetMapping("/{accountNumber}")
    public List<TransactionResponse> transactionHistory(
            @PathVariable String accountNumber) {

        return transactionService.transactionHistory(accountNumber);
    }

    @PostMapping("/transfer")
    public List<TransactionResponse> transfer(@RequestBody TransferRequest request){
        return transactionService.transfer(request);
    }
}
