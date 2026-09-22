package com.BankingSystem.Banking_System.SecurityTest;

import com.BankingSystem.Banking_System.config.JWTAuthenticationFilter;
import com.BankingSystem.Banking_System.entity.User;
import com.BankingSystem.Banking_System.repository.UserRepository;
import com.BankingSystem.Banking_System.service.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class JWTAuthenticationFilterTest {

    private JwtService jwtService;
    private UserRepository userRepository;
    private JWTAuthenticationFilter filter;

    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        userRepository = mock(UserRepository.class);

        filter = new JWTAuthenticationFilter(
                jwtService,
                userRepository
        );

        filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @Test
    void validToken_shouldAuthenticateUser() throws Exception {

        User user = new User();

        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPassword("Password123");

        String token = "valid-token";

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.extractUsername(token))
                .thenReturn(user.getEmail());

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(jwtService.isTokenValid(token, user))
                .thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertNotNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void missingAuthorizationHeader_shouldContinueFilterChain() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verify(filterChain).doFilter(request, response);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userRepository);
    }

    @Test
    void invalidToken_shouldNotAuthenticateUser() throws Exception {

        User user = new User();

        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPassword("Password123");

        String token = "invalid-token";

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.extractUsername(token))
                .thenReturn(user.getEmail());

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(jwtService.isTokenValid(token, user))
                .thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void userNotFound_shouldNotAuthenticateUser() throws Exception {

        String token = "valid-token";

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.extractUsername(token))
                .thenReturn("unknown@example.com");

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        filter.doFilter(request, response, filterChain);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verify(filterChain).doFilter(request, response);

        verify(jwtService, never())
                .isTokenValid(anyString(), any(User.class));
    }
}