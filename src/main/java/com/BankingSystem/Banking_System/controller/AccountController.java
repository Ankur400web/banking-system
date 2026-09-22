package com.BankingSystem.Banking_System.controller;


import com.BankingSystem.Banking_System.dto.AccountResponse;
import com.BankingSystem.Banking_System.dto.CreateAccountRequest;
import com.BankingSystem.Banking_System.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/accounts")
@RestController
@Tag(
        name = "Accounts",
        description = "Account creation, retrieval and management"
)
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    @PostMapping
    @Operation(
            summary = "Create a bank account",
            description = "Creates a new bank account for the authenticated user"
    )
    public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest request){
        return accountService.createAccount(request);
    }

    @GetMapping("/id/{id}")
    @Operation(
            summary = "Get account by ID",
            description = "Retrieves an account by its ID after verifying account ownership"
    )
    public AccountResponse getAccount(@PathVariable Long id){
        return accountService.getAccountById(id);
    }

    @GetMapping("/{accountNumber}")
    @Operation(
            summary = "Get account by account number",
            description = "Retrieves an account using its account number"
    )
    public AccountResponse getAccountByAccountNumber(@PathVariable String accountNumber) {
        return accountService.getAccountByAccountNumber(accountNumber);
    }

    @GetMapping
    public List<AccountResponse> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @DeleteMapping("/{accountNumber}")
    @Operation(
            summary = "Delete bank account",
            description = "Deletes an account when the account belongs to the authenticated user and has a zero balance"
    )
    public ResponseEntity<Void> deleteAccount(@Valid @PathVariable String accountNumber) {
        accountService.deleteAccount(accountNumber);
        return ResponseEntity.noContent().build();
    }
}
