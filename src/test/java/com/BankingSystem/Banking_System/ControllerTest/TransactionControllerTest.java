package com.BankingSystem.Banking_System.ControllerTest;

import com.BankingSystem.Banking_System.controller.TransactionController;
import com.BankingSystem.Banking_System.dto.DepositRequest;
import com.BankingSystem.Banking_System.dto.TransactionResponse;
import com.BankingSystem.Banking_System.dto.TransferRequest;
import com.BankingSystem.Banking_System.dto.WithdrawRequest;
import com.BankingSystem.Banking_System.enums.TransactionTypes;
import com.BankingSystem.Banking_System.exception.AccountNotFoundException;
import com.BankingSystem.Banking_System.exception.InsufficientBalanceException;
import com.BankingSystem.Banking_System.exception.SameAccountException;
import com.BankingSystem.Banking_System.exception.UnauthorizedAccountAccessException;
import com.BankingSystem.Banking_System.service.TransactionService;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.BankingSystem.Banking_System.service.JwtService;
import com.BankingSystem.Banking_System.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser
    void deposit_shouldReturnTransactionResponse() throws Exception {

        DepositRequest request = new DepositRequest();
        request.setAccountNumber("1234567890");
        request.setAmount(new BigDecimal("500.00"));

        TransactionResponse response = new TransactionResponse();
        response.setId(1L);
        response.setAccountNumber("1234567890");
        response.setType(TransactionTypes.DEPOSIT);
        response.setAmount(new BigDecimal("500.00"));
        response.setBalanceAfter(new BigDecimal("1500.00"));

        when(transactionService.deposit(any(DepositRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/transaction/deposit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber")
                        .value("1234567890"))
                .andExpect(jsonPath("$.type")
                        .value("DEPOSIT"))
                .andExpect(jsonPath("$.amount")
                        .value(500.00))
                .andExpect(jsonPath("$.balanceAfter")
                        .value(1500.00));

        verify(transactionService)
                .deposit(any(DepositRequest.class));
    }

    @Test
    void deposit_shouldRejectBlankAccountNumber() throws Exception {
        DepositRequest request = new DepositRequest();
        request.setAccountNumber("");
        request.setAmount(new BigDecimal("500.00"));

        mockMvc.perform(
                        post("/api/transaction/deposit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).deposit(any(DepositRequest.class));
    }

    @Test
    void deposit_shouldReturn404WhenAccountNotFound() throws Exception {

        DepositRequest request = new DepositRequest();
        request.setAccountNumber("1234567890");
        request.setAmount(new BigDecimal("500.00"));

        when(transactionService.deposit(any(DepositRequest.class)))
                .thenThrow(new AccountNotFoundException("Account not found"));

        mockMvc.perform(
                        post("/api/transaction/deposit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());

        verify(transactionService).deposit(any(DepositRequest.class));
    }

    @Test
    void withdraw_shouldReturnTransactionResponse() throws Exception {

        WithdrawRequest request = new WithdrawRequest();
        request.setAccountNumber("1234567890");
        request.setAmount(new BigDecimal("500.00"));

        TransactionResponse response = new TransactionResponse();
        response.setId(2L);
        response.setAccountNumber("1234567890");
        response.setType(TransactionTypes.WITHDRAWAL);
        response.setAmount(new BigDecimal("500.00"));
        response.setBalanceAfter(new BigDecimal("500.00"));

        when(transactionService.withDraw(any(WithdrawRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/transaction/withdraw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andExpect(jsonPath("$.type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$.amount").value(500.00))
                .andExpect(jsonPath("$.balanceAfter").value(500.00));

        verify(transactionService).withDraw(any(WithdrawRequest.class));
    }

    @Test
    void withdraw_shouldRejectBlankAccountNumber() throws Exception {

        WithdrawRequest request = new WithdrawRequest();
        request.setAccountNumber("");
        request.setAmount(new BigDecimal("500.00"));

        mockMvc.perform(
                        post("/api/transaction/withdraw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).withDraw(any(WithdrawRequest.class));
    }

    @Test
    void withdraw_shouldRejectMissingAmount() throws Exception {

        WithdrawRequest request = new WithdrawRequest();
        request.setAccountNumber("1234567890");

        mockMvc.perform(
                        post("/api/transaction/withdraw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).withDraw(any(WithdrawRequest.class));
    }

    @Test
    void withdraw_shouldRejectZeroAmount() throws Exception {

        WithdrawRequest request = new WithdrawRequest();
        request.setAccountNumber("1234567890");
        request.setAmount(BigDecimal.ZERO);

        mockMvc.perform(
                        post("/api/transaction/withdraw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).withDraw(any(WithdrawRequest.class));
    }

    @Test
    void withdraw_shouldRejectNegativeAmount() throws Exception {

        WithdrawRequest request = new WithdrawRequest();
        request.setAccountNumber("1234567890");
        request.setAmount(new BigDecimal("-100.00"));

        mockMvc.perform(
                        post("/api/transaction/withdraw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).withDraw(any(WithdrawRequest.class));
    }

    @Test
    void withdraw_shouldReturn404WhenAccountNotFound() throws Exception {

        WithdrawRequest request = new WithdrawRequest();
        request.setAccountNumber("1234567890");
        request.setAmount(new BigDecimal("500.00"));

        when(transactionService.withDraw(any(WithdrawRequest.class)))
                .thenThrow(new AccountNotFoundException("Account not found"));

        mockMvc.perform(
                        post("/api/transaction/withdraw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());

        verify(transactionService).withDraw(any(WithdrawRequest.class));
    }

    @Test
    void withdraw_shouldReturn400WhenInsufficientBalance() throws Exception {

        WithdrawRequest request = new WithdrawRequest();
        request.setAccountNumber("1234567890");
        request.setAmount(new BigDecimal("5000.00"));

        when(transactionService.withDraw(any(WithdrawRequest.class)))
                .thenThrow(new InsufficientBalanceException("Insufficient balance"));

        mockMvc.perform(
                        post("/api/transaction/withdraw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(transactionService).withDraw(any(WithdrawRequest.class));
    }

    @Test
    void transfer_shouldReturnTransactionResponses() throws Exception {

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("1111111111");
        request.setToAccountNumber("2222222222");
        request.setAmount(new BigDecimal("500.00"));

        TransactionResponse sourceResponse = new TransactionResponse();
        sourceResponse.setId(3L);
        sourceResponse.setAccountNumber("1111111111");
        sourceResponse.setType(TransactionTypes.WITHDRAWAL);
        sourceResponse.setAmount(new BigDecimal("500.00"));
        sourceResponse.setBalanceAfter(new BigDecimal("500.00"));

        TransactionResponse destinationResponse = new TransactionResponse();
        destinationResponse.setId(4L);
        destinationResponse.setAccountNumber("2222222222");
        destinationResponse.setType(TransactionTypes.DEPOSIT);
        destinationResponse.setAmount(new BigDecimal("500.00"));
        destinationResponse.setBalanceAfter(new BigDecimal("1500.00"));

        when(transactionService.transfer(any(TransferRequest.class)))
                .thenReturn(List.of(sourceResponse, destinationResponse));

        mockMvc.perform(
                        post("/api/transaction/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("1111111111"))
                .andExpect(jsonPath("$[0].type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$[0].amount").value(500.00))
                .andExpect(jsonPath("$[0].balanceAfter").value(500.00))
                .andExpect(jsonPath("$[1].accountNumber").value("2222222222"))
                .andExpect(jsonPath("$[1].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[1].amount").value(500.00))
                .andExpect(jsonPath("$[1].balanceAfter").value(1500.00));

        verify(transactionService).transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_shouldRejectBlankSourceAccount() throws Exception {

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("");
        request.setToAccountNumber("2222222222");
        request.setAmount(new BigDecimal("500.00"));

        mockMvc.perform(
                        post("/api/transaction/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_shouldRejectBlankDestinationAccount() throws Exception {

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("1111111111");
        request.setToAccountNumber("");
        request.setAmount(new BigDecimal("500.00"));

        mockMvc.perform(
                        post("/api/transaction/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_shouldRejectMissingAmount() throws Exception {

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("1111111111");
        request.setToAccountNumber("2222222222");

        mockMvc.perform(
                        post("/api/transaction/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_shouldRejectZeroAmount() throws Exception {

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("1111111111");
        request.setToAccountNumber("2222222222");
        request.setAmount(BigDecimal.ZERO);

        mockMvc.perform(
                        post("/api/transaction/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_shouldRejectNegativeAmount() throws Exception {

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("1111111111");
        request.setToAccountNumber("2222222222");
        request.setAmount(new BigDecimal("-100.00"));

        mockMvc.perform(
                        post("/api/transaction/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_shouldReturn404WhenSourceAccountNotFound() throws Exception {

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("1111111111");
        request.setToAccountNumber("2222222222");
        request.setAmount(new BigDecimal("500.00"));

        when(transactionService.transfer(any(TransferRequest.class)))
                .thenThrow(new AccountNotFoundException("Account not found"));

        mockMvc.perform(
                        post("/api/transaction/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());

        verify(transactionService).transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_shouldReturn400WhenInsufficientBalance() throws Exception {

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("1111111111");
        request.setToAccountNumber("2222222222");
        request.setAmount(new BigDecimal("5000.00"));

        when(transactionService.transfer(any(TransferRequest.class)))
                .thenThrow(new InsufficientBalanceException("Insufficient balance"));

        mockMvc.perform(
                        post("/api/transaction/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(transactionService).transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_shouldReturn400WhenSameAccount() throws Exception {

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("1111111111");
        request.setToAccountNumber("1111111111");
        request.setAmount(new BigDecimal("500.00"));

        when(transactionService.transfer(any(TransferRequest.class)))
                .thenThrow(new SameAccountException("Source and destination accounts cannot be the same"));

        mockMvc.perform(
                        post("/api/transaction/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(transactionService).transfer(any(TransferRequest.class));
    }

    @Test
    void transactionHistory_shouldReturnTransactionHistory() throws Exception {

        TransactionResponse response = new TransactionResponse();
        response.setId(1L);
        response.setAccountNumber("1234567890");
        response.setType(TransactionTypes.DEPOSIT);
        response.setAmount(new BigDecimal("500.00"));
        response.setBalanceAfter(new BigDecimal("1500.00"));

        when(transactionService.transactionHistory("1234567890"))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/transaction/1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("1234567890"))
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[0].amount").value(500.00))
                .andExpect(jsonPath("$[0].balanceAfter").value(1500.00));

        verify(transactionService).transactionHistory("1234567890");
    }

    @Test
    void transactionHistory_shouldReturn404WhenAccountNotFound() throws Exception {

        when(transactionService.transactionHistory("1234567890"))
                .thenThrow(new AccountNotFoundException("Account not found"));

        mockMvc.perform(
                        get("/api/transaction/1234567890")
                )
                .andExpect(status().isNotFound());

        verify(transactionService).transactionHistory("1234567890");
    }

    @Test
    void transactionHistory_shouldReturn403WhenUnauthorized() throws Exception {

        when(transactionService.transactionHistory("1234567890"))
                .thenThrow(
                        new UnauthorizedAccountAccessException(
                                "You are not authorized to do this"
                        )
                );

        mockMvc.perform(
                        get("/api/transaction/1234567890")
                )
                .andExpect(status().isForbidden());

        verify(transactionService).transactionHistory("1234567890");
    }

    @Test
    void transactionHistory_shouldReturnEmptyListWhenNoTransactions() throws Exception {

        when(transactionService.transactionHistory("1234567890"))
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/transaction/1234567890")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(transactionService).transactionHistory("1234567890");
    }
}
