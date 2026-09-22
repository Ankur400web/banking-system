package com.BankingSystem.Banking_System.controller;

import com.BankingSystem.Banking_System.dto.DepositRequest;
import com.BankingSystem.Banking_System.dto.TransactionResponse;
import com.BankingSystem.Banking_System.dto.TransferRequest;
import com.BankingSystem.Banking_System.dto.WithdrawRequest;
import com.BankingSystem.Banking_System.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(
            summary = "Deposit money",
            description = "Deposits money into the authenticated user's account"
    )
    public TransactionResponse deposit(@Valid @RequestBody DepositRequest depositRequest){
        return transactionService.deposit(depositRequest);
    }

    @PostMapping("/withdraw")
    @Operation(
            summary = "Withdraw money",
            description = "Withdraws money from the authenticated user's account after checking the available balance"
    )
    public TransactionResponse withdraw(@Valid @RequestBody WithdrawRequest request){
        return transactionService.withDraw(request);
    }

    @GetMapping("/{accountNumber}")
    @Operation(
            summary = "Get transaction history",
            description = "Retrieves the transaction history for an account after verifying account ownership"
    )
    public List<TransactionResponse> transactionHistory(
            @PathVariable String accountNumber) {

        return transactionService.transactionHistory(accountNumber);
    }

    @PostMapping("/transfer")
    @Operation(
            summary = "Transfer money",
            description = "Transfers money from the authenticated user's account to another account"
    )
    public List<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request){
        return transactionService.transfer(request);
    }
}
