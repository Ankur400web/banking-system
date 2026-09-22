package com.BankingSystem.Banking_System.ControllerTest;

import com.BankingSystem.Banking_System.controller.AccountController;
import com.BankingSystem.Banking_System.dto.AccountResponse;
import com.BankingSystem.Banking_System.dto.CreateAccountRequest;
import com.BankingSystem.Banking_System.exception.AccountNotFoundException;
import com.BankingSystem.Banking_System.exception.UnauthorizedAccountAccessException;
import com.BankingSystem.Banking_System.exception.UserNotFoundException;
import com.BankingSystem.Banking_System.service.AccountService;
import com.BankingSystem.Banking_System.service.JwtService;
import com.BankingSystem.Banking_System.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;


    @Test
    void createAccount_shouldReturnAccountResponse() throws Exception {

        CreateAccountRequest request = new CreateAccountRequest();
        request.setUserId(22L);
        request.setAccountType("SAVINGS");

        AccountResponse response = new AccountResponse();
        response.setId(1L);
        response.setAccountNumber("1234567890");
        response.setAccountType("SAVINGS");
        response.setBalance(new BigDecimal("0.00"));
        response.setUserId(22L);

        when(accountService.createAccount(any(CreateAccountRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andExpect(jsonPath("$.accountType").value("SAVINGS"))
                .andExpect(jsonPath("$.balance").value(0.00))
                .andExpect(jsonPath("$.userId").value(22));

        verify(accountService).createAccount(any(CreateAccountRequest.class));
    }

    @Test
    void createAccount_shouldRejectInvalidRequest() throws Exception {

        CreateAccountRequest request = new CreateAccountRequest();
        request.setUserId(null);
        request.setAccountType("");

        mockMvc.perform(
                        post("/api/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(accountService, never())
                .createAccount(any(CreateAccountRequest.class));
    }
    @Test
    void createAccount_shouldReturn404WhenUserNotFound() throws Exception {

        CreateAccountRequest request = new CreateAccountRequest();
        request.setUserId(999L);
        request.setAccountType("SAVINGS");

        when(accountService.createAccount(any(CreateAccountRequest.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(
                        post("/api/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());

        verify(accountService).createAccount(any(CreateAccountRequest.class));
    }

    @Test
    void getAccount_shouldReturnAccountResponse() throws Exception {

        AccountResponse response = new AccountResponse();
        response.setId(1L);
        response.setAccountNumber("1234567890");
        response.setAccountType("SAVINGS");
        response.setBalance(new BigDecimal("1000.00"));
        response.setUserId(22L);

        when(accountService.getAccountById(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/accounts/id/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andExpect(jsonPath("$.accountType").value("SAVINGS"))
                .andExpect(jsonPath("$.balance").value(1000.00))
                .andExpect(jsonPath("$.userId").value(22));

        verify(accountService).getAccountById(1L);
    }

    @Test
    void getAccount_shouldReturn404WhenAccountNotFound() throws Exception {

        when(accountService.getAccountById(999L))
                .thenThrow(new AccountNotFoundException("Account not found"));

        mockMvc.perform(
                        get("/api/accounts/id/999")
                )
                .andExpect(status().isNotFound());

        verify(accountService).getAccountById(999L);
    }

    @Test
    void getAccount_shouldReturn403WhenUnauthorized() throws Exception {

        when(accountService.getAccountById(1L))
                .thenThrow(
                        new UnauthorizedAccountAccessException(
                                "You are not authorized to do this"
                        )
                );

        mockMvc.perform(
                        get("/api/accounts/id/1")
                )
                .andExpect(status().isForbidden());

        verify(accountService).getAccountById(1L);
    }

    @Test
    void getAccountByNumber_shouldReturnAccountResponse() throws Exception {

        AccountResponse response = new AccountResponse();
        response.setId(1L);
        response.setAccountNumber("1234567890");
        response.setAccountType("SAVINGS");
        response.setBalance(new BigDecimal("1000.00"));
        response.setUserId(22L);

        when(accountService.getAccountByAccountNumber("1234567890"))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/accounts/1234567890")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andExpect(jsonPath("$.accountType").value("SAVINGS"))
                .andExpect(jsonPath("$.balance").value(1000.00))
                .andExpect(jsonPath("$.userId").value(22));

        verify(accountService).getAccountByAccountNumber("1234567890");
    }

    @Test
    void getAccountByNumber_shouldReturn404WhenAccountNotFound() throws Exception {

        when(accountService.getAccountByAccountNumber("9999999999"))
                .thenThrow(new AccountNotFoundException("Account not found"));

        mockMvc.perform(
                        get("/api/accounts/9999999999")
                )
                .andExpect(status().isNotFound());

        verify(accountService).getAccountByAccountNumber("9999999999");
    }

    @Test
    void deleteAccount_shouldReturn204WhenSuccessful() throws Exception {

        doNothing().when(accountService)
                .deleteAccount("1234567890");

        mockMvc.perform(
                        delete("/api/accounts/1234567890")
                )
                .andExpect(status().isNoContent());

        verify(accountService).deleteAccount("1234567890");
    }

    @Test
    void deleteAccount_shouldReturn404WhenAccountNotFound() throws Exception {

        doThrow(new AccountNotFoundException("Account not found"))
                .when(accountService)
                .deleteAccount("9999999999");

        mockMvc.perform(
                        delete("/api/accounts/9999999999")
                )
                .andExpect(status().isNotFound());

        verify(accountService).deleteAccount("9999999999");
    }

    @Test
    void deleteAccount_shouldReturn400WhenBalanceIsNotZero() throws Exception {

        doThrow(new IllegalStateException("Account balance must be zero"))
                .when(accountService)
                .deleteAccount("1234567890");

        mockMvc.perform(
                        delete("/api/accounts/1234567890")
                )
                .andExpect(status().isBadRequest());

        verify(accountService).deleteAccount("1234567890");
    }

    @Test
    void createAccount_shouldRejectMissingAccountType() throws Exception {

        CreateAccountRequest request = new CreateAccountRequest();
        request.setUserId(22L);
        request.setAccountType(null);

        mockMvc.perform(
                        post("/api/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(accountService, never())
                .createAccount(any(CreateAccountRequest.class));
    }

    @Test
    void createAccount_shouldRejectMissingUserId() throws Exception {

        CreateAccountRequest request = new CreateAccountRequest();
        request.setUserId(null);
        request.setAccountType("SAVINGS");

        mockMvc.perform(
                        post("/api/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(accountService, never())
                .createAccount(any(CreateAccountRequest.class));
    }

    @Test
    void getAccountByNumber_shouldReturn403WhenUnauthorized() throws Exception {

        when(accountService.getAccountByAccountNumber("1234567890"))
                .thenThrow(
                        new UnauthorizedAccountAccessException(
                                "You are not authorized to do this"
                        )
                );

        mockMvc.perform(
                        get("/api/accounts/1234567890")
                )
                .andExpect(status().isForbidden());

        verify(accountService)
                .getAccountByAccountNumber("1234567890");
    }
}
