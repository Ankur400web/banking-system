package com.BankingSystem.Banking_System.service;

import com.BankingSystem.Banking_System.dto.AccountResponse;
import com.BankingSystem.Banking_System.dto.CreateAccountRequest;
import com.BankingSystem.Banking_System.entity.Account;
import com.BankingSystem.Banking_System.entity.User;
import com.BankingSystem.Banking_System.exception.AccountNotFoundException;
import com.BankingSystem.Banking_System.exception.UserNotFoundException;
import com.BankingSystem.Banking_System.repository.AccountRepository;
import com.BankingSystem.Banking_System.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;


@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;


    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public AccountResponse createAccount(CreateAccountRequest request){
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new UserNotFoundException("User doesn't exist"));

        String accountNumber = generateAccountNumber();

        Account account = new Account();

        account.setAccountNumber(accountNumber);
        account.setAccountType(request.getAccountType());
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);


        Account savedAccount = accountRepository.save(account);

        AccountResponse accountResponse = new AccountResponse();

        accountResponse.setId(savedAccount.getId());
        accountResponse.setAccountNumber(savedAccount.getAccountNumber());
        accountResponse.setAccountType(savedAccount.getAccountType());
        accountResponse.setBalance(savedAccount.getBalance());
        accountResponse.setCreatedAt(savedAccount.getCreatedAt());
        accountResponse.setUserId(savedAccount.getUser().getId());

        return accountResponse;



    }

    private String generateAccountNumber() {



        Random random = new Random();

        long number = 1000000000L + random.nextLong(900000000L);

        String accountNumber = String.valueOf(number);

        if (accountRepository.existsByAccountNumber(String.valueOf(accountNumber))) {
            return generateAccountNumber();
        }

        return accountNumber    ;
    }


    public AccountResponse getAccountById(Long accId){
        Account account = accountRepository.findById(accId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        AccountResponse accountResponse = new AccountResponse();

        accountResponse.setId(account.getId());
        accountResponse.setAccountNumber(account.getAccountNumber());
        accountResponse.setAccountType(account.getAccountType());
        accountResponse.setBalance(account.getBalance());
        accountResponse.setCreatedAt(account.getCreatedAt());
        accountResponse.setUserId(account.getUser().getId());

        return accountResponse;
    }

    public AccountResponse getAccountByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        AccountResponse accountResponse = new AccountResponse();

        accountResponse.setId(account.getId());
        accountResponse.setAccountNumber(account.getAccountNumber());
        accountResponse.setAccountType(account.getAccountType());
        accountResponse.setBalance(account.getBalance());
        accountResponse.setCreatedAt(account.getCreatedAt());
        accountResponse.setUserId(account.getUser().getId());

        return accountResponse;
    }

    public List<AccountResponse> getAllAccounts(){

        List<Account> accounts = accountRepository.findAll();

        return accounts.stream().map(account -> {
            AccountResponse accountResponse = new AccountResponse();
            accountResponse.setId(account.getId());
            accountResponse.setAccountNumber(account.getAccountNumber());
            accountResponse.setAccountType(account.getAccountType());
            accountResponse.setBalance(account.getBalance());
            accountResponse.setCreatedAt(account.getCreatedAt());
            accountResponse.setUserId(account.getUser().getId());
            return accountResponse;
        }).toList();
    }

    public void deleteAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Cannot delete account with non-zero balance");
        }
        accountRepository.delete(account);
    }
}

