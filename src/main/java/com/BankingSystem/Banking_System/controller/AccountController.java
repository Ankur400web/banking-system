package com.BankingSystem.Banking_System.controller;


import com.BankingSystem.Banking_System.dto.AccountResponse;
import com.BankingSystem.Banking_System.dto.CreateAccountRequest;
import com.BankingSystem.Banking_System.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/accounts")
@RestController
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    @PostMapping
    public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest request){
        return accountService.createAccount(request);
    }

    @GetMapping("/id/{id}")
    public AccountResponse getAccount(@PathVariable Long id){
        return accountService.getAccountById(id);
    }

    @GetMapping("/{accountNumber}")
    public AccountResponse getAccountByAccountNumber(@PathVariable String accountNumber) {
        return accountService.getAccountByAccountNumber(accountNumber);
    }

    @GetMapping
    public List<AccountResponse> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String accountNumber) {
        accountService.deleteAccount(accountNumber);
        return ResponseEntity.noContent().build();
    }
}
