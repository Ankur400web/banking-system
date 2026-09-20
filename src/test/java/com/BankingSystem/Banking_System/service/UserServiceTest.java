package com.BankingSystem.Banking_System.service;

import com.BankingSystem.Banking_System.dto.CreateUserRequest;
import com.BankingSystem.Banking_System.dto.UserResponse;
import com.BankingSystem.Banking_System.entity.User;
import com.BankingSystem.Banking_System.exception.DuplicateEmailException;
import com.BankingSystem.Banking_System.repository.AccountRepository;
import com.BankingSystem.Banking_System.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


    @Test
    void createUser_shouldCreateUserSuccessfully(){
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("Manish");
        request.setLastName("Bhardwaj");
        request.setEmail("manish@example.com");
        request.setPassword("password123");

        User savedUser = new User();

        savedUser.setId(1L);
        savedUser.setFirstName("Manish");
        savedUser.setLastName("Bhardwaj");
        savedUser.setEmail("manish@example.com");
        savedUser.setPassword("encodedPassword");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);


        when(passwordEncoder.encode(request.getPassword())).thenReturn("enocdedPassword");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Manish", response.getFirstName());
        assertEquals("Bhardwaj", response.getLastName());
        assertEquals("manish@example.com", response.getEmail());

        verify(userRepository).existsByEmail(response.getEmail());

        verify(passwordEncoder).encode(request.getPassword());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowDuplicateEmailException() {

        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("Manish");
        request.setLastName("Bhardwaj");
        request.setEmail("manish@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> userService.createUser(request)
        );

        verify(userRepository).existsByEmail(request.getEmail());

        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(anyString());
    }
}
