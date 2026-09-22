package com.BankingSystem.Banking_System.service;

import com.BankingSystem.Banking_System.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.BankingSystem.Banking_System.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void generateToken_shouldReturnToken() {

        User user = new User();

        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPassword("Password123");

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertTrue(!token.isBlank());
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {

        User user = new User();

        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPassword("Password123");

        String token = jwtService.generateToken(user);

        boolean result =
                jwtService.isTokenValid(token, user);

        assertTrue(result);
    }

    @Test
    void isTokenValid_shouldReturnFalseForDifferentUser() {

        User user = new User();

        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPassword("Password123");

        User differentUser = new User();

        differentUser.setId(2L);
        differentUser.setFirstName("Jane");
        differentUser.setLastName("Doe");
        differentUser.setEmail("jane@example.com");
        differentUser.setPassword("Password123");

        String token = jwtService.generateToken(user);

        boolean result =
                jwtService.isTokenValid(token, differentUser);

        assertTrue(!result);
    }
}