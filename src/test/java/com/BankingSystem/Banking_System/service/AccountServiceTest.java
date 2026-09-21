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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    AccountRepository accountRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    AccountService accountService;

    @Mock
    Authentication authentication;

    @Mock
    SecurityContext securityContext;

    @Test
    void createAccount_shouldCreateSuccessfully() {

        CreateAccountRequest request = new CreateAccountRequest();
        request.setUserId(1L);
        request.setAccountType("SAVINGS");

        User user = new User();
        user.setId(1L);
        user.setFirstName("Ankur");
        user.setLastName("Kumar");
        user.setEmail("ankur@example.com");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("1234567890");
        account.setAccountType("SAVINGS");
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);

        when(accountRepository.save(any(Account.class)))
                .thenReturn(account);

        AccountResponse response = accountService.createAccount(request);

        assertEquals(1L, response.getId());
        assertEquals("1234567890", response.getAccountNumber());
        assertEquals("SAVINGS", response.getAccountType());
        assertEquals(BigDecimal.ZERO, response.getBalance());
        assertEquals(1L, response.getUserId());

        verify(userRepository).findById(1L);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_shouldThrowUserNotFoundException() {

        CreateAccountRequest request = new CreateAccountRequest();
        request.setUserId(1L);
        request.setAccountType("SAVINGS");

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> accountService.createAccount(request)
        );

        verify(userRepository).findById(1L);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void getAccountById_shouldReturnAccountSuccessfully() {

        Long accountId = 1L;

        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(accountId);
        account.setAccountNumber("1234567890");
        account.setAccountType("SAVINGS");
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(user);

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        AccountResponse response = accountService.getAccountById(accountId);

        assertEquals(1L, response.getId());
        assertEquals("1234567890", response.getAccountNumber());
        assertEquals("SAVINGS", response.getAccountType());
        assertEquals(BigDecimal.ZERO, response.getBalance());
        assertEquals(1L, response.getUserId());

        verify(accountRepository).findById(accountId);
    }

    @Test
    void getAccountById_shouldThrowAccountNotFoundException() {

        Long accountId = 1L;

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getAccountById(accountId)
        );

        verify(accountRepository).findById(accountId);
    }

    @Test
    void getAccountById_shouldThrowUnauthorizedAccountAccessException() {

        Long accountId = 1L;

        User accountOwner = new User();
        accountOwner.setId(2L);

        User authenticatedUser = new User();
        authenticatedUser.setId(1L);

        Account account = new Account();
        account.setId(accountId);
        account.setAccountNumber("1234567890");
        account.setAccountType("SAVINGS");
        account.setBalance(BigDecimal.ZERO);
        account.setUser(accountOwner);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(authenticatedUser);

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        assertThrows(
                UnauthorizedAccountAccessException.class,
                () -> accountService.getAccountById(accountId)
        );

        verify(accountRepository).findById(accountId);
    }

    @Test
    void getAccountByNumber_shouldReturnAccountSuccessfully() {

        String accountNumber = "1234567890";

        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber(accountNumber);
        account.setAccountType("SAVINGS");
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(user);

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));

        AccountResponse response =
                accountService.getAccountByAccountNumber(accountNumber);

        assertEquals(1L, response.getId());
        assertEquals(accountNumber, response.getAccountNumber());
        assertEquals("SAVINGS", response.getAccountType());
        assertEquals(BigDecimal.ZERO, response.getBalance());
        assertEquals(1L, response.getUserId());

        verify(accountRepository).findByAccountNumber(accountNumber);
    }

    @Test
    void getAccountByNumber_shouldThrowAccountNotFoundException() {

        String accountNumber = "1234567890";

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getAccountByAccountNumber(accountNumber)
        );

        verify(accountRepository).findByAccountNumber(accountNumber);
    }

    @Test
    void getAccountByNumber_shouldThrowUnauthorizedAccountAccessException() {

        String accountNumber = "1234567890";

        User accountOwner = new User();
        accountOwner.setId(2L);

        User authenticatedUser = new User();
        authenticatedUser.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber(accountNumber);
        account.setAccountType("SAVINGS");
        account.setBalance(BigDecimal.ZERO);
        account.setUser(accountOwner);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(authenticatedUser);

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));

        assertThrows(
                UnauthorizedAccountAccessException.class,
                () -> accountService.getAccountByAccountNumber(accountNumber)
        );

        verify(accountRepository).findByAccountNumber(accountNumber);
    }

    @Test
    void deleteAccount_shouldDeleteSuccessfully() {

        String accountNumber = "1234567890";

        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber(accountNumber);
        account.setAccountType("SAVINGS");
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));

        accountService.deleteAccount(accountNumber);

        verify(accountRepository).findByAccountNumber(accountNumber);
        verify(accountRepository).delete(account);
    }

    @Test
    void deleteAccount_shouldThrowAccountNotFoundException() {

        String accountNumber = "1234567890";

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.deleteAccount(accountNumber)
        );

        verify(accountRepository).findByAccountNumber(accountNumber);
        verify(accountRepository, never()).delete(any(Account.class));
    }

    @Test
    void deleteAccount_shouldThrowIllegalStateException_whenBalanceIsNotZero() {

        String accountNumber = "1234567890";

        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber(accountNumber);
        account.setAccountType("SAVINGS");
        account.setBalance(new BigDecimal("500.00"));
        account.setUser(user);

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.of(account));

        assertThrows(
                IllegalStateException.class,
                () -> accountService.deleteAccount(accountNumber)
        );

        verify(accountRepository).findByAccountNumber(accountNumber);
        verify(accountRepository, never()).delete(any(Account.class));
    }
}