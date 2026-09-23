package com.BankingSystem.Banking_System.service;

import com.BankingSystem.Banking_System.dto.DepositRequest;
import com.BankingSystem.Banking_System.dto.TransactionResponse;
import com.BankingSystem.Banking_System.dto.TransferRequest;
import com.BankingSystem.Banking_System.dto.WithdrawRequest;
import com.BankingSystem.Banking_System.entity.Account;
import com.BankingSystem.Banking_System.entity.Transaction;
import com.BankingSystem.Banking_System.entity.User;
import com.BankingSystem.Banking_System.enums.TransactionTypes;
import com.BankingSystem.Banking_System.exception.AccountNotFoundException;
import com.BankingSystem.Banking_System.exception.InsufficientBalanceException;
import com.BankingSystem.Banking_System.exception.SameAccountException;
import com.BankingSystem.Banking_System.exception.UnauthorizedAccountAccessException;
import com.BankingSystem.Banking_System.repository.AccountRepository;
import com.BankingSystem.Banking_System.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    @Transactional
    public TransactionResponse deposit(DepositRequest deposit){
        Account account = accountRepository.findByAccountNumberForUpdate(deposit.getAccountNumber())
                .orElseThrow(()-> {
                    log.warn("Fund Deposit Failed: Account not found");
                    return new AccountNotFoundException("Account not found");
                });

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        if (!account.getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized attempt to access another account");
            throw new UnauthorizedAccountAccessException(
                    "You are not authorized to access this account"
            );
        }


        BigDecimal newBalance = account.getBalance().add(deposit.getAmount());

        account.setBalance(newBalance);
        log.info("Balance Updated");

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

        log.info("Amount deposited successfully");
        return response;


    }

    @Transactional
    public TransactionResponse withDraw(WithdrawRequest request){
        Account account = accountRepository.findByAccountNumberForUpdate(request.getAccountNumber())
                .orElseThrow(()-> {
                    log.warn("Withdrawal Failed: Account not found");
                    return new AccountNotFoundException("Account Not found");
                });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        if (!account.getUser().getId().equals(user.getId())){
            log.warn("Unauthorized attempt to withdraw from another account");
            throw new UnauthorizedAccountAccessException("You are not authorized to this account.");
        }

        if (request.getAmount().compareTo(account.getBalance())>0){
            throw new InsufficientBalanceException("Insufficient balance");
        }

        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());

        account.setBalance(newBalance);
        log.info("Balance Updated after withdrawal");

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

        log.info("Amount Withdraw Successful");
        return response;


    }

    public List<TransactionResponse> transactionHistory(String accNum){
        Account account = accountRepository.findByAccountNumber(accNum)
                .orElseThrow(()-> {
                    log.warn("Transaction History Retrieval Failed: Account not found");
                    return new AccountNotFoundException("Account doesn't exist");
                });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User authenticatedUser = (User) authentication.getPrincipal();

        if (!account.getUser().getId().equals(authenticatedUser.getId())){
            log.warn("Unauthorized attempt to retrieve history from another account");
            throw new UnauthorizedAccountAccessException("You are not authorized to check transaction history");
        }

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
        log.info("Retrieved {} Transaction History", transactions.size());

        return responses;
    }

    @Transactional
    public List<TransactionResponse> transfer(TransferRequest request){
        Account fromAccount = accountRepository.findByAccountNumberForUpdate(request.getFromAccountNumber())
                .orElseThrow(()-> {
                    log.warn("Transfer failed: source account not found");
                    return new AccountNotFoundException("Source account doesn't exist");
                });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        if (!fromAccount.getUser().getId().equals(user.getId())){
            log.warn("Unauthorized attempt to transfer money");
            throw new UnauthorizedAccountAccessException("You are not authorized to transfer money");
        }
        
        Account toAccount = accountRepository.findByAccountNumberForUpdate(request.getToAccountNumber())
                .orElseThrow(()-> {
                    log.warn("Transfer failed: destination account not found");
                    return new AccountNotFoundException("Destination account not found");
                });
        
        if (fromAccount.getAccountNumber().equals(toAccount.getAccountNumber())){
            log.warn("Transfer failed: Not Allowed to transfer to the same account");
            throw new SameAccountException("Cannot transfer to same account");
        } else if (request.getAmount().compareTo(fromAccount.getBalance())>0) {
            log.warn("Transfer failed: Insufficient Balance");
            throw new InsufficientBalanceException("Insufficient funds to transfer");
        }

        BigDecimal newFromBalance = fromAccount.getBalance().subtract(request.getAmount());

        BigDecimal newToBalance = toAccount.getBalance().add(request.getAmount());


        fromAccount.setBalance(newFromBalance);
        log.info("Source account balance updated");

        toAccount.setBalance(newToBalance);
        log.info("Destination account balance updated");

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction fromTransaction = new Transaction();

        fromTransaction.setType(TransactionTypes.WITHDRAWAL);
        fromTransaction.setAmount(request.getAmount());
        fromTransaction.setBalanceAfter(newFromBalance);
        fromTransaction.setAccount(fromAccount);

        Transaction toTransaction = new Transaction();

        toTransaction.setType(TransactionTypes.DEPOSIT);
        toTransaction.setAmount(request.getAmount());
        toTransaction.setBalanceAfter(newToBalance);
        toTransaction.setAccount(toAccount);

        transactionRepository.save(fromTransaction);
        transactionRepository.save(toTransaction);

        TransactionResponse fromResponse = new TransactionResponse();
        fromResponse.setId(fromTransaction.getId());
        fromResponse.setAccountNumber(fromAccount.getAccountNumber());
        fromResponse.setType(fromTransaction.getType());
        fromResponse.setAmount(fromTransaction.getAmount());
        fromResponse.setBalanceAfter(fromTransaction.getBalanceAfter());
        fromResponse.setCreatedAt(fromTransaction.getCreatedAt());

        TransactionResponse toResponse = new TransactionResponse();
        toResponse.setId(toTransaction.getId());
        toResponse.setAccountNumber(toAccount.getAccountNumber());
        toResponse.setType(toTransaction.getType());
        toResponse.setAmount(toTransaction.getAmount());
        toResponse.setBalanceAfter(toTransaction.getBalanceAfter());
        toResponse.setCreatedAt(toTransaction.getCreatedAt());

        List<TransactionResponse> responses = new ArrayList<>();
        responses.add(fromResponse);
        responses.add(toResponse);

        log.info("Transfer from {} to {} amount {}",
                request.getFromAccountNumber(),
                request.getToAccountNumber(),
                request.getAmount());
        return responses;


    }
}
