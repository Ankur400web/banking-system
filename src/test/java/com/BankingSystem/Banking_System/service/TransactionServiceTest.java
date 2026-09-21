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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    AccountRepository accountRepository;

    @Mock
    Authentication authentication;

    @Mock
    SecurityContext securityContext;

    @InjectMocks
    TransactionService transactionService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deposit_shouldDepositSuccessfully() {

        DepositRequest request = new DepositRequest();
        request.setAccountNumber("1234567890");
        request.setAmount(new BigDecimal("500.00"));

        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("1234567890");
        account.setAccountType("SAVINGS");
        account.setBalance(new BigDecimal("1000.00"));
        account.setUser(user);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(user);

        when(accountRepository.findByAccountNumberForUpdate("1234567890"))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(account))
                .thenReturn(account);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.deposit(request);

        assertEquals("1234567890", response.getAccountNumber());
        assertEquals(TransactionTypes.DEPOSIT, response.getType());
        assertEquals(new BigDecimal("500.00"), response.getAmount());
        assertEquals(new BigDecimal("1500.00"), response.getBalanceAfter());
        assertEquals(new BigDecimal("1500.00"), account.getBalance());

        verify(accountRepository)
                .findByAccountNumberForUpdate("1234567890");

        verify(accountRepository)
                .save(account);

        verify(transactionRepository)
                .save(any(Transaction.class));
    }

    @Test
    void deposit_shouldThrowAccountNotFoundException() {

        DepositRequest request = new DepositRequest();
        request.setAccountNumber("1234567890");
        request.setAmount(new BigDecimal("500.00"));

        when(accountRepository.findByAccountNumberForUpdate("1234567890"))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> transactionService.deposit(request)
        );

        verify(accountRepository)
                .findByAccountNumberForUpdate("1234567890");

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void deposit_shouldThrowUnauthorizedAccountAccessException() {

        DepositRequest request = new DepositRequest();
        request.setAccountNumber("1234567890");
        request.setAmount(new BigDecimal("500.00"));

        User accountOwner = new User();
        accountOwner.setId(2L);

        User authenticatedUser = new User();
        authenticatedUser.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("1234567890");
        account.setAccountType("SAVINGS");
        account.setBalance(new BigDecimal("1000.00"));
        account.setUser(accountOwner);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(authenticatedUser);

        when(accountRepository.findByAccountNumberForUpdate("1234567890"))
                .thenReturn(Optional.of(account));

        assertThrows(
                UnauthorizedAccountAccessException.class,
                () -> transactionService.deposit(request)
        );

        assertEquals(
                new BigDecimal("1000.00"),
                account.getBalance()
        );

        verify(accountRepository)
                .findByAccountNumberForUpdate("1234567890");

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void withDraw_shouldWithdrawSuccessfully() {

        WithdrawRequest request = new WithdrawRequest();
        request.setAccountNumber("1234567890");
        request.setAmount(new BigDecimal("400.00"));

        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("1234567890");
        account.setAccountType("SAVINGS");
        account.setBalance(new BigDecimal("1000.00"));
        account.setUser(user);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(user);

        when(accountRepository.findByAccountNumberForUpdate("1234567890"))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(account))
                .thenReturn(account);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.withDraw(request);

        assertEquals("1234567890", response.getAccountNumber());
        assertEquals(TransactionTypes.WITHDRAWAL, response.getType());
        assertEquals(new BigDecimal("400.00"), response.getAmount());
        assertEquals(new BigDecimal("600.00"), response.getBalanceAfter());
        assertEquals(new BigDecimal("600.00"), account.getBalance());

        verify(accountRepository)
                .findByAccountNumberForUpdate("1234567890");

        verify(accountRepository)
                .save(account);

        verify(transactionRepository)
                .save(any(Transaction.class));
    }

    @Test
    void withDraw_shouldThrowInsufficientBalanceException() {

        WithdrawRequest request = new WithdrawRequest();
        request.setAccountNumber("1234567890");
        request.setAmount(new BigDecimal("1500.00"));

        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("1234567890");
        account.setAccountType("SAVINGS");
        account.setBalance(new BigDecimal("1000.00"));
        account.setUser(user);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(user);

        when(accountRepository.findByAccountNumberForUpdate("1234567890"))
                .thenReturn(Optional.of(account));

        assertThrows(
                InsufficientBalanceException.class,
                () -> transactionService.withDraw(request)
        );

        assertEquals(
                new BigDecimal("1000.00"),
                account.getBalance()
        );

        verify(accountRepository)
                .findByAccountNumberForUpdate("1234567890");

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void withDraw_shouldThrowUnauthorizedAccountAccessException() {

        WithdrawRequest request = new WithdrawRequest();
        request.setAccountNumber("1234567890");
        request.setAmount(new BigDecimal("400.00"));

        User accountOwner = new User();
        accountOwner.setId(2L);

        User authenticatedUser = new User();
        authenticatedUser.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("1234567890");
        account.setAccountType("SAVINGS");
        account.setBalance(new BigDecimal("1000.00"));
        account.setUser(accountOwner);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(authenticatedUser);

        when(accountRepository.findByAccountNumberForUpdate("1234567890"))
                .thenReturn(Optional.of(account));

        assertThrows(
                UnauthorizedAccountAccessException.class,
                () -> transactionService.withDraw(request)
        );

        assertEquals(
                new BigDecimal("1000.00"),
                account.getBalance()
        );

        verify(accountRepository)
                .findByAccountNumberForUpdate("1234567890");

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transfer_shouldTransferSuccessfully() {

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("1111111111");
        request.setToAccountNumber("2222222222");
        request.setAmount(new BigDecimal("400.00"));

        User user = new User();
        user.setId(1L);

        Account fromAccount = new Account();
        fromAccount.setId(1L);
        fromAccount.setAccountNumber("1111111111");
        fromAccount.setAccountType("SAVINGS");
        fromAccount.setBalance(new BigDecimal("1000.00"));
        fromAccount.setUser(user);

        User receiver = new User();
        receiver.setId(2L);

        Account toAccount = new Account();
        toAccount.setId(2L);
        toAccount.setAccountNumber("2222222222");
        toAccount.setAccountType("SAVINGS");
        toAccount.setBalance(new BigDecimal("500.00"));
        toAccount.setUser(receiver);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(user);

        when(accountRepository.findByAccountNumberForUpdate("1111111111"))
                .thenReturn(Optional.of(fromAccount));

        when(accountRepository.findByAccountNumberForUpdate("2222222222"))
                .thenReturn(Optional.of(toAccount));

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<TransactionResponse> responses =
                transactionService.transfer(request);

        assertEquals(2, responses.size());

        assertEquals(
                new BigDecimal("600.00"),
                fromAccount.getBalance()
        );

        assertEquals(
                new BigDecimal("900.00"),
                toAccount.getBalance()
        );

        assertEquals(
                TransactionTypes.WITHDRAWAL,
                responses.get(0).getType()
        );

        assertEquals(
                TransactionTypes.DEPOSIT,
                responses.get(1).getType()
        );

        assertEquals(
                new BigDecimal("400.00"),
                responses.get(0).getAmount()
        );

        assertEquals(
                new BigDecimal("400.00"),
                responses.get(1).getAmount()
        );

        assertEquals(
                new BigDecimal("600.00"),
                responses.get(0).getBalanceAfter()
        );

        assertEquals(
                new BigDecimal("900.00"),
                responses.get(1).getBalanceAfter()
        );

        verify(accountRepository)
                .findByAccountNumberForUpdate("1111111111");

        verify(accountRepository)
                .findByAccountNumberForUpdate("2222222222");

        verify(accountRepository).save(fromAccount);
        verify(accountRepository).save(toAccount);

        verify(transactionRepository, times(2))
                .save(any(Transaction.class));
    }

    @Test
    void transfer_shouldThrowSourceAccountNotFoundException() {

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("1111111111");
        request.setToAccountNumber("2222222222");
        request.setAmount(new BigDecimal("400.00"));

        when(accountRepository.findByAccountNumberForUpdate("1111111111"))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> transactionService.transfer(request)
        );

        verify(accountRepository)
                .findByAccountNumberForUpdate("1111111111");

        verify(accountRepository, never())
                .save(any(Account.class));

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }

    @Test
    void transfer_shouldThrowUnauthorizedAccountAccessException() {

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("1111111111");
        request.setToAccountNumber("2222222222");
        request.setAmount(new BigDecimal("400.00"));

        User accountOwner = new User();
        accountOwner.setId(2L);

        User authenticatedUser = new User();
        authenticatedUser.setId(1L);

        Account fromAccount = new Account();
        fromAccount.setId(1L);
        fromAccount.setAccountNumber("1111111111");
        fromAccount.setAccountType("SAVINGS");
        fromAccount.setBalance(new BigDecimal("1000.00"));
        fromAccount.setUser(accountOwner);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(authenticatedUser);

        when(accountRepository.findByAccountNumberForUpdate("1111111111"))
                .thenReturn(Optional.of(fromAccount));

        assertThrows(
                UnauthorizedAccountAccessException.class,
                () -> transactionService.transfer(request)
        );

        assertEquals(
                new BigDecimal("1000.00"),
                fromAccount.getBalance()
        );

        verify(accountRepository)
                .findByAccountNumberForUpdate("1111111111");

        verify(accountRepository, never())
                .findByAccountNumberForUpdate("2222222222");

        verify(accountRepository, never())
                .save(any(Account.class));

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }
    @Test
    void transfer_shouldThrowDestinationAccountNotFoundException() {

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("1111111111");
        request.setToAccountNumber("2222222222");
        request.setAmount(new BigDecimal("400.00"));

        User user = new User();
        user.setId(1L);

        Account fromAccount = new Account();
        fromAccount.setId(1L);
        fromAccount.setAccountNumber("1111111111");
        fromAccount.setAccountType("SAVINGS");
        fromAccount.setBalance(new BigDecimal("1000.00"));
        fromAccount.setUser(user);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(user);

        when(accountRepository.findByAccountNumberForUpdate("1111111111"))
                .thenReturn(Optional.of(fromAccount));

        when(accountRepository.findByAccountNumberForUpdate("2222222222"))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> transactionService.transfer(request)
        );

        assertEquals(
                new BigDecimal("1000.00"),
                fromAccount.getBalance()
        );

        verify(accountRepository)
                .findByAccountNumberForUpdate("1111111111");

        verify(accountRepository)
                .findByAccountNumberForUpdate("2222222222");

        verify(accountRepository, never())
                .save(any(Account.class));

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }

    @Test
    void transfer_shouldThrowSameAccountException() {

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("1111111111");
        request.setToAccountNumber("1111111111");
        request.setAmount(new BigDecimal("400.00"));

        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("1111111111");
        account.setAccountType("SAVINGS");
        account.setBalance(new BigDecimal("1000.00"));
        account.setUser(user);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(user);

        when(accountRepository.findByAccountNumberForUpdate("1111111111"))
                .thenReturn(Optional.of(account));

        assertThrows(
                SameAccountException.class,
                () -> transactionService.transfer(request)
        );

        assertEquals(
                new BigDecimal("1000.00"),
                account.getBalance()
        );

        verify(accountRepository, times(2))
                .findByAccountNumberForUpdate("1111111111");

        verify(accountRepository, never())
                .save(any(Account.class));

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }

    @Test
    void transfer_shouldThrowInsufficientBalanceException() {

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("1111111111");
        request.setToAccountNumber("2222222222");
        request.setAmount(new BigDecimal("1500.00"));

        User user = new User();
        user.setId(1L);

        Account fromAccount = new Account();
        fromAccount.setId(1L);
        fromAccount.setAccountNumber("1111111111");
        fromAccount.setAccountType("SAVINGS");
        fromAccount.setBalance(new BigDecimal("1000.00"));
        fromAccount.setUser(user);

        Account toAccount = new Account();
        toAccount.setId(2L);
        toAccount.setAccountNumber("2222222222");
        toAccount.setAccountType("SAVINGS");
        toAccount.setBalance(new BigDecimal("500.00"));
        toAccount.setUser(user);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(user);

        when(accountRepository.findByAccountNumberForUpdate("1111111111"))
                .thenReturn(Optional.of(fromAccount));

        when(accountRepository.findByAccountNumberForUpdate("2222222222"))
                .thenReturn(Optional.of(toAccount));

        assertThrows(
                InsufficientBalanceException.class,
                () -> transactionService.transfer(request)
        );

        assertEquals(
                new BigDecimal("1000.00"),
                fromAccount.getBalance()
        );

        assertEquals(
                new BigDecimal("500.00"),
                toAccount.getBalance()
        );

        verify(accountRepository)
                .findByAccountNumberForUpdate("1111111111");

        verify(accountRepository)
                .findByAccountNumberForUpdate("2222222222");

        verify(accountRepository, never())
                .save(any(Account.class));

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }

    @Test
    void transactionHistory_shouldReturnTransactionHistory() {

        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("1234567890");
        account.setAccountType("SAVINGS");
        account.setBalance(new BigDecimal("1500.00"));
        account.setUser(user);

        Transaction deposit = new Transaction();
        deposit.setId(1L);
        deposit.setType(TransactionTypes.DEPOSIT);
        deposit.setAmount(new BigDecimal("500.00"));
        deposit.setBalanceAfter(new BigDecimal("1500.00"));
        deposit.setAccount(account);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(user);

        when(accountRepository.findByAccountNumber("1234567890"))
                .thenReturn(Optional.of(account));

        when(transactionRepository.findAllByAccount(account))
                .thenReturn(List.of(deposit));

        List<TransactionResponse> responses =
                transactionService.transactionHistory("1234567890");

        assertEquals(1, responses.size());

        TransactionResponse response = responses.get(0);

        assertEquals("1234567890", response.getAccountNumber());
        assertEquals(TransactionTypes.DEPOSIT, response.getType());
        assertEquals(new BigDecimal("500.00"), response.getAmount());
        assertEquals(
                new BigDecimal("1500.00"),
                response.getBalanceAfter()
        );

        verify(accountRepository)
                .findByAccountNumber("1234567890");

        verify(transactionRepository)
                .findAllByAccount(account);
    }

    @Test
    void transactionHistory_shouldThrowAccountNotFoundException() {

        when(accountRepository.findByAccountNumber("1234567890"))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> transactionService.transactionHistory("1234567890")
        );

        verify(accountRepository)
                .findByAccountNumber("1234567890");

        verify(transactionRepository, never())
                .findAllByAccount(any(Account.class));
    }

    @Test
    void transactionHistory_shouldThrowUnauthorizedAccountAccessException() {

        User accountOwner = new User();
        accountOwner.setId(2L);

        User authenticatedUser = new User();
        authenticatedUser.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("1234567890");
        account.setAccountType("SAVINGS");
        account.setBalance(new BigDecimal("1500.00"));
        account.setUser(accountOwner);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(authenticatedUser);

        when(accountRepository.findByAccountNumber("1234567890"))
                .thenReturn(Optional.of(account));

        assertThrows(
                UnauthorizedAccountAccessException.class,
                () -> transactionService.transactionHistory("1234567890")
        );

        verify(accountRepository)
                .findByAccountNumber("1234567890");

        verify(transactionRepository, never())
                .findAllByAccount(any(Account.class));
    }

    @Test
    void transactionHistory_shouldReturnEmptyList() {

        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("1234567890");
        account.setAccountType("SAVINGS");
        account.setBalance(new BigDecimal("1000.00"));
        account.setUser(user);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(user);

        when(accountRepository.findByAccountNumber("1234567890"))
                .thenReturn(Optional.of(account));

        when(transactionRepository.findAllByAccount(account))
                .thenReturn(List.of());

        List<TransactionResponse> responses =
                transactionService.transactionHistory("1234567890");

        assertEquals(0, responses.size());

        verify(accountRepository)
                .findByAccountNumber("1234567890");

        verify(transactionRepository)
                .findAllByAccount(account);
    }
}