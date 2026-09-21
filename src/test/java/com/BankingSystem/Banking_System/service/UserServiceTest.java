package com.BankingSystem.Banking_System.service;

import com.BankingSystem.Banking_System.dto.ChangePasswordRequest;
import com.BankingSystem.Banking_System.dto.CreateUserRequest;
import com.BankingSystem.Banking_System.dto.UpdateUserRequest;
import com.BankingSystem.Banking_System.dto.UserResponse;
import com.BankingSystem.Banking_System.entity.User;
import com.BankingSystem.Banking_System.exception.DuplicateEmailException;
import com.BankingSystem.Banking_System.exception.InvalidPasswordException;
import com.BankingSystem.Banking_System.exception.UserNotFoundException;
import com.BankingSystem.Banking_System.repository.AccountRepository;
import com.BankingSystem.Banking_System.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

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

    @Mock
    Authentication authentication;

    @Mock
    SecurityContext securityContext;

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

    @Test
    void getUserById_shouldReturnSuccessfully(){

        Long UserId = 1L;

        User user = new User();

        user.setId(UserId);
        user.setFirstName("Ankur");
        user.setLastName("kumar");
        user.setEmail("ankur@example.com");
        user.setPassword("password");

        when(userRepository.findById(UserId))
                .thenReturn(Optional.of(user));

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(user);

        UserResponse response = userService.getUserById(UserId);

        assertEquals(UserId, response.getId());
        assertEquals("Ankur", response.getFirstName());
        assertEquals("kumar", response.getLastName());
        assertEquals("ankur@example.com", response.getEmail());

        verify(userRepository).findById(UserId);
    }

    @Test
    void getUserById_shouldThrowUserNotFoundException(){

        Long UserId = 999L;

        when(userRepository.findById(UserId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                ()-> userService.getUserById(UserId)
        );


        verify(userRepository).findById(UserId);
    }

    @Test
    void getAllUsers_shouldReturnUsers() {

        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("Ankur");
        user1.setLastName("Kumar");
        user1.setEmail("ankur@example.com");
        user1.setPassword("encodedPassword");

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Manish");
        user2.setLastName("Bhardwaj");
        user2.setEmail("manish@example.com");
        user2.setPassword("encodedPassword");

        List<User> users = List.of(user1, user2);

        when(userRepository.findAll())
                .thenReturn(users);

        List<UserResponse> responses = userService.getAllUsers();

        assertEquals(2, responses.size());

        assertEquals(1L, responses.get(0).getId());
        assertEquals("Ankur", responses.get(0).getFirstName());
        assertEquals("Kumar", responses.get(0).getLastName());
        assertEquals("ankur@example.com", responses.get(0).getEmail());

        assertEquals(2L, responses.get(1).getId());
        assertEquals("Manish", responses.get(1).getFirstName());
        assertEquals("Bhardwaj", responses.get(1).getLastName());
        assertEquals("manish@example.com", responses.get(1).getEmail());

        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_shouldReturnEmptyList(){

        when(userRepository.findAll())
                .thenReturn(List.of());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.getAllUsers()
        );

        verify(userRepository).findAll();
    }

    @Test
    void updateUser_shouldUpdateSuccessfully() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setFirstName("Ankur");
        user.setLastName("Kumar");
        user.setEmail("old@example.com");
        user.setPassword("encodedPassword");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("Rahul");
        request.setLastName("Sharma");
        request.setEmail("rahul@example.com");

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(user);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        UserResponse response = userService.updateUser(userId, request);

        assertEquals(userId, response.getId());
        assertEquals("Rahul", response.getFirstName());
        assertEquals("Sharma", response.getLastName());
        assertEquals("rahul@example.com", response.getEmail());

        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_shouldThrowUserNotFoundException() {

        Long userId = 1L;

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("Rahul");
        request.setLastName("Sharma");
        request.setEmail("rahul@example.com");

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser(userId, request)
        );

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_shouldDeleteSuccessfully() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setFirstName("Ankur");
        user.setLastName("Kumar");
        user.setEmail("ankur@example.com");

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(user);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(accountRepository.existsByUser(user))
                .thenReturn(false);

        userService.deleteUserById(userId);

        verify(userRepository).findById(userId);
        verify(accountRepository).existsByUser(user);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_shouldThrowUserNotFoundException() {

        Long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.deleteUserById(userId)
        );

        verify(userRepository).findById(userId);
        verify(userRepository, never()).delete(any(User.class));
    }


    @Test
    void changePassword_shouldChangePasswordSuccessfully() {

        User user = new User();
        user.setId(1L);
        user.setEmail("ankur@example.com");
        user.setPassword("oldEncodedPassword");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newPassword123");

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(user);

        when(passwordEncoder.matches("oldPassword", "oldEncodedPassword"))
                .thenReturn(true);

        when(passwordEncoder.matches("newPassword123", "oldEncodedPassword"))
                .thenReturn(false);

        when(passwordEncoder.encode("newPassword123"))
                .thenReturn("newEncodedPassword");

        userService.changePassword(request);

        assertEquals("newEncodedPassword", user.getPassword());

        verify(passwordEncoder).matches(
                "oldPassword",
                "oldEncodedPassword"
        );

        verify(passwordEncoder).matches(
                "newPassword123",
                "oldEncodedPassword"
        );

        verify(passwordEncoder).encode("newPassword123");

        verify(userRepository).save(user);
    }

    @Test
    void changePassword_shouldThrowInvalidPasswordException_whenCurrentPasswordIsWrong() {

        User user = new User();
        user.setId(1L);
        user.setEmail("ankur@example.com");
        user.setPassword("oldEncodedPassword");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrongPassword");
        request.setNewPassword("newPassword123");

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(user);

        when(passwordEncoder.matches("wrongPassword", "oldEncodedPassword"))
                .thenReturn(false);

        assertThrows(
                InvalidPasswordException.class,
                () -> userService.changePassword(request)
        );

        verify(passwordEncoder).matches(
                "wrongPassword",
                "oldEncodedPassword"
        );

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

}
