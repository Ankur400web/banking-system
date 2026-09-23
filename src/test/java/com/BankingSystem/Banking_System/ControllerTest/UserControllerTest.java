package com.BankingSystem.Banking_System.ControllerTest;

import com.BankingSystem.Banking_System.controller.UserController;
import com.BankingSystem.Banking_System.dto.ChangePasswordRequest;
import com.BankingSystem.Banking_System.dto.CreateUserRequest;
import com.BankingSystem.Banking_System.dto.UpdateUserRequest;
import com.BankingSystem.Banking_System.dto.UserResponse;
import com.BankingSystem.Banking_System.exception.DuplicateEmailException;
import com.BankingSystem.Banking_System.exception.UnauthorizedAccountAccessException;
import com.BankingSystem.Banking_System.exception.UserHasAccountException;
import com.BankingSystem.Banking_System.exception.UserNotFoundException;
import com.BankingSystem.Banking_System.service.JwtService;
import com.BankingSystem.Banking_System.service.UserService;
import com.BankingSystem.Banking_System.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

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
    void getAllUsers_shouldReturnUsers() throws Exception {

        UserResponse user1 = new UserResponse();
        user1.setId(1L);
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setEmail("john@example.com");

        UserResponse user2 = new UserResponse();
        user2.setId(2L);
        user2.setFirstName("Jane");
        user2.setLastName("Doe");
        user2.setEmail("jane@example.com");

        when(userService.getAllUsers())
                .thenReturn(List.of(user1, user2));

        mockMvc.perform(
                        get("/api/users")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].email").value("jane@example.com"));

        verify(userService).getAllUsers();
    }

    @Test
    void getAllUsers_shouldReturnEmptyList() throws Exception {

        when(userService.getAllUsers())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/users")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(userService).getAllUsers();
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {

        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setFirstName("John");
        response.setLastName("Doe");
        response.setEmail("john@example.com");

        when(userService.getUserById(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/users/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService).getUserById(1L);
    }

    @Test
    void getUserById_shouldReturn404WhenUserNotFound() throws Exception {

        when(userService.getUserById(999L))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(
                        get("/api/users/999")
                )
                .andExpect(status().isNotFound());

        verify(userService).getUserById(999L);
    }

    @Test
    void createUser_shouldRejectInvalidRequest() throws Exception {

        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("");
        request.setLastName("");
        request.setEmail("invalid-email");
        request.setPassword("123");

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(userService, never())
                .createUser(any(CreateUserRequest.class));
    }

    @Test
    void createUser_shouldReturn409WhenEmailAlreadyExists() throws Exception {

        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPassword("Password123");

        when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new DuplicateEmailException("Email already exists"));

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict());

        verify(userService).createUser(any(CreateUserRequest.class));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("John");
        request.setLastName("Updated");
        request.setEmail("john.updated@example.com");

        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setFirstName("John");
        response.setLastName("Updated");
        response.setEmail("john.updated@example.com");

        when(userService.updateUser(
                org.mockito.ArgumentMatchers.eq(1L),
                any(UpdateUserRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@example.com"));

        verify(userService).updateUser(
                org.mockito.ArgumentMatchers.eq(1L),
                any(UpdateUserRequest.class)
        );
    }

    @Test
    void updateUser_shouldRejectInvalidRequest() throws Exception {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("");
        request.setLastName("");
        request.setEmail("invalid-email");

        mockMvc.perform(
                        put("/api/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(userService, never())
                .updateUser(
                        org.mockito.ArgumentMatchers.eq(1L),
                        any(UpdateUserRequest.class)
                );
    }

    @Test
    void updateUser_shouldReturn404WhenUserNotFound() throws Exception {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("John");
        request.setLastName("Updated");
        request.setEmail("john.updated@example.com");

        when(userService.updateUser(
                org.mockito.ArgumentMatchers.eq(999L),
                any(UpdateUserRequest.class)
        )).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(
                        put("/api/users/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());

        verify(userService).updateUser(
                org.mockito.ArgumentMatchers.eq(999L),
                any(UpdateUserRequest.class)
        );
    }

    @Test
    void updateUser_shouldReturn409WhenEmailAlreadyExists() throws Exception {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("John");
        request.setLastName("Updated");
        request.setEmail("existing@example.com");

        when(userService.updateUser(
                org.mockito.ArgumentMatchers.eq(1L),
                any(UpdateUserRequest.class)
        )).thenThrow(new DuplicateEmailException("Email already exists"));

        mockMvc.perform(
                        put("/api/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict());

        verify(userService).updateUser(
                org.mockito.ArgumentMatchers.eq(1L),
                any(UpdateUserRequest.class)
        );
    }

    @Test
    void deleteUser_shouldReturn200WhenSuccessful() throws Exception {

        doNothing().when(userService)
                .deleteUserById(1L);

        mockMvc.perform(
                        delete("/api/users/1")
                )
                .andExpect(status().isOk());

        verify(userService).deleteUserById(1L);
    }

    @Test
    void deleteUser_shouldReturn404WhenUserNotFound() throws Exception {

        doThrow(new UserNotFoundException("User not found"))
                .when(userService)
                .deleteUserById(999L);

        mockMvc.perform(
                        delete("/api/users/999")
                )
                .andExpect(status().isNotFound());

        verify(userService).deleteUserById(999L);
    }

    @Test
    void deleteUser_shouldReturn409WhenUserHasAccounts() throws Exception {

        doThrow(new UserHasAccountException("Cannot delete user while accounts exist"))
                .when(userService)
                .deleteUserById(1L);

        mockMvc.perform(
                        delete("/api/users/1")
                )
                .andExpect(status().isConflict());

        verify(userService).deleteUserById(1L);
    }

    @Test
    void updateUser_shouldReturn403WhenUnauthorized() throws Exception {

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("John");
        request.setLastName("Updated");
        request.setEmail("john.updated@example.com");

        when(userService.updateUser(
                org.mockito.ArgumentMatchers.eq(1L),
                any(UpdateUserRequest.class)
        )).thenThrow(
                new UnauthorizedAccountAccessException(
                        "You are not authorized to do this"
                )
        );

        mockMvc.perform(
                        put("/api/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden());

        verify(userService).updateUser(
                org.mockito.ArgumentMatchers.eq(1L),
                any(UpdateUserRequest.class)
        );
    }

    @Test
    void getUserById_shouldReturn403WhenUnauthorized() throws Exception {

        when(userService.getUserById(1L))
                .thenThrow(
                        new UnauthorizedAccountAccessException(
                                "You are not authorized to do this"
                        )
                );

        mockMvc.perform(
                        get("/api/users/1")
                )
                .andExpect(status().isForbidden());

        verify(userService).getUserById(1L);
    }
    @Test
    void changePassword_shouldReturn200WhenSuccessful() throws Exception {

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("OldPassword123");
        request.setNewPassword("NewPassword123");

        doNothing().when(userService)
                .changePassword(any(ChangePasswordRequest.class));

        mockMvc.perform(
                        put("/api/users/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNoContent());

        verify(userService).changePassword(any(ChangePasswordRequest.class));
    }

}