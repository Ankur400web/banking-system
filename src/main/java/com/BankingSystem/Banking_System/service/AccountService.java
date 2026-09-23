package com.BankingSystem.Banking_System.service;

import com.BankingSystem.Banking_System.dto.AccountResponse;
import com.BankingSystem.Banking_System.dto.CreateAccountRequest;
import com.BankingSystem.Banking_System.entity.Account;
import com.BankingSystem.Banking_System.entity.User;
import com.BankingSystem.Banking_System.exception.AccountNotFoundException;
import com.BankingSystem.Banking_System.exception.UnauthorizedAccountAccessException;
import com.BankingSystem.Banking_System.exception.UserNotFoundException;
import com.BankingSystem.Banking_System.repository.AccountRepository;
import com.BankingSystem.Banking_System.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log =
            LoggerFactory.getLogger(AccountService.class);


    public AccountResponse createAccount(CreateAccountRequest request){
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.warn("Account Creation Failed: User not found");
                    return new UserNotFoundException("User doesn't exist");
                });

        String accountNumber = generateAccountNumber();

        Account account = new Account();

        account.setAccountNumber(accountNumber);
        account.setAccountType(request.getAccountType());
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);


        Account savedAccount = accountRepository.save(account);
        log.info("Account saved");

        AccountResponse accountResponse = new AccountResponse();

        accountResponse.setId(savedAccount.getId());
        accountResponse.setAccountNumber(savedAccount.getAccountNumber());
        accountResponse.setAccountType(savedAccount.getAccountType());
        accountResponse.setBalance(savedAccount.getBalance());
        accountResponse.setCreatedAt(savedAccount.getCreatedAt());
        accountResponse.setUserId(savedAccount.getUser().getId());

        log.info("Account created successfully");
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
                .orElseThrow(() ->{
                    log.warn("Account Retrieval Failed: Account doesn't exist");
                    return new AccountNotFoundException("Account not found");
                });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        if(!account.getUser().getId().equals(user.getId())){
            log.warn("Unauthorized attempt to retrieve another account");
            throw new UnauthorizedAccountAccessException("You are not authorized to do this");
        }

        AccountResponse accountResponse = new AccountResponse();

        accountResponse.setId(account.getId());
        accountResponse.setAccountNumber(account.getAccountNumber());
        accountResponse.setAccountType(account.getAccountType());
        accountResponse.setBalance(account.getBalance());
        accountResponse.setCreatedAt(account.getCreatedAt());
        accountResponse.setUserId(account.getUser().getId());

        log.info("Account retrieved successfully");
        return accountResponse;
    }

    public AccountResponse getAccountByAccountNumber(String accountNumber){
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() ->{
                    log.warn("Account Retrieval Failed: AccountNumber Not found");
                    return new AccountNotFoundException("Account not found");
                });

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        if (!account.getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized attempt to retrieve another account by account number");
            throw new UnauthorizedAccountAccessException(
                    "You are not authorized to access this account"
            );
        }

        AccountResponse accountResponse = new AccountResponse();

        accountResponse.setId(account.getId());
        accountResponse.setAccountNumber(account.getAccountNumber());
        accountResponse.setAccountType(account.getAccountType());
        accountResponse.setBalance(account.getBalance());
        accountResponse.setCreatedAt(account.getCreatedAt());
        accountResponse.setUserId(account.getUser().getId());

        log.info("Account Retrieved Successfully");
        return accountResponse;
    }

    public List<AccountResponse> getAllAccounts(){

        List<Account> accounts = accountRepository.findAll();

        log.info("Retrieved {} Accounts", accounts.size());

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
                .orElseThrow(() -> {
                    log.warn("Account Deletion Failed: Account doesn't exist");
                    return new AccountNotFoundException("Account not found");
                });

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            log.warn("Account Deletion Failed: Balance must be zero");
            throw new IllegalStateException("Cannot delete account with non-zero balance");
        }

        log.info("Account deleted Successfully");
        accountRepository.delete(account);
    }
}

