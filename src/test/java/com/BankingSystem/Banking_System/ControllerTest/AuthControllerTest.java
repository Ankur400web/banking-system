package com.BankingSystem.Banking_System.ControllerTest;

import com.BankingSystem.Banking_System.controller.AuthController;
import com.BankingSystem.Banking_System.dto.LoginRequest;
import com.BankingSystem.Banking_System.dto.LoginResponse;
import com.BankingSystem.Banking_System.exception.InvalidCredentialsException;
import com.BankingSystem.Banking_System.exception.UserNotFoundException;
import com.BankingSystem.Banking_System.repository.UserRepository;
import com.BankingSystem.Banking_System.service.JwtService;
import com.BankingSystem.Banking_System.service.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;


    @Test
    void login_shouldReturnLoginResponse() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("Password123");

        LoginResponse response = new LoginResponse();
        response.setToken("test-jwt-token");

        when(userService.userLogin(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"));

        verify(userService).userLogin(any(LoginRequest.class));
    }

    @Test
    void login_shouldReturn401WhenCredentialsAreInvalid() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("WrongPassword");

        when(userService.userLogin(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Invalid credentials"));

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized());

        verify(userService).userLogin(any(LoginRequest.class));
    }

    @Test
    void login_shouldReturn404WhenUserNotFound() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("Password123");

        when(userService.userLogin(any(LoginRequest.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());

        verify(userService).userLogin(any(LoginRequest.class));
    }
    @Test
    void login_shouldRejectInvalidRequest() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("");
        request.setPassword("");

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(userService, never())
                .userLogin(any(LoginRequest.class));
    }

}