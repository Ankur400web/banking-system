package com.BankingSystem.Banking_System.service;

import com.BankingSystem.Banking_System.dto.DepositRequest;
import com.BankingSystem.Banking_System.dto.TransactionResponse;
import com.BankingSystem.Banking_System.dto.WithdrawRequest;
import com.BankingSystem.Banking_System.entity.Account;
import com.BankingSystem.Banking_System.entity.Transaction;
import com.BankingSystem.Banking_System.enums.TransactionTypes;
import com.BankingSystem.Banking_System.exception.AccountNotFoundException;
import com.BankingSystem.Banking_System.exception.InsufficientBalanceException;
import com.BankingSystem.Banking_System.repository.AccountRepository;
import com.BankingSystem.Banking_System.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository){
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public TransactionResponse deposit(DepositRequest deposit){
        Account account = accountRepository.findByAccountNumber(deposit.getAccountNumber())
                .orElseThrow(()-> new AccountNotFoundException("Account not found"));


        BigDecimal newBalance = account.getBalance().add(deposit.getAmount());

        account.setBalance(newBalance);

        Transaction transaction = new Transaction();

        transaction.setType(TransactionTypes.DEPOSIT);
        transaction.setAmount(deposit.getAmount());
        transaction.setBalanceAfter(newBalance);
        transaction.setAccount(account);

        accountRepository.save(account);
        transactionRepository.save(transaction);

        TransactionResponse response = new TransactionResponse();

        response.setId(transaction.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setType(transaction.getType());
        response.setAmount(transaction.getAmount());
        response.setBalanceAfter(transaction.getBalanceAfter());
        response.setCreatedAt(transaction.getCreatedAt());

        return response;


    }

    @Transactional
    public TransactionResponse withDraw(WithdrawRequest request){
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(()-> new AccountNotFoundException("Account Not found"));

        if (request.getAmount().compareTo(account.getBalance())>0){
            throw new InsufficientBalanceException("Insufficient balance");
        }

        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());

        account.setBalance(newBalance);

        Transaction transaction = new Transaction();

        transaction.setType(TransactionTypes.WITHDRAWAL);
        transaction.setAmount(request.getAmount());
        transaction.setBalanceAfter(newBalance);
        transaction.setAccount(account);

        accountRepository.save(account);
        transactionRepository.save(transaction);

        TransactionResponse response = new TransactionResponse();

        response.setId(transaction.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setType(transaction.getType());
        response.setAmount(transaction.getAmount());
        response.setBalanceAfter(transaction.getBalanceAfter());
        response.setCreatedAt(transaction.getCreatedAt());

        return response;


    }

    public List<TransactionResponse> transactionHistory(String accNum){
        Account account = accountRepository.findByAccountNumber(accNum)
                .orElseThrow(()-> new AccountNotFoundException("Account doesn't exist"));

        List<Transaction> transactions = transactionRepository.findAllByAccount(account);

        List<TransactionResponse> responses = new ArrayList<>();

        for (Transaction transaction: transactions){

                TransactionResponse response = new TransactionResponse();
                response.setId(transaction.getId());
                response.setAccountNumber(account.getAccountNumber());
                response.setType(transaction.getType());
                response.setAmount(transaction.getAmount());
                response.setBalanceAfter(transaction.getBalanceAfter());
                response.setCreatedAt(transaction.getCreatedAt());

                responses.add(response);
        }
        return responses;
    }
}
