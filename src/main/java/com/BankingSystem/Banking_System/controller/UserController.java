package com.BankingSystem.Banking_System.controller;

import com.BankingSystem.Banking_System.dto.ChangePasswordRequest;
import com.BankingSystem.Banking_System.dto.CreateUserRequest;
import com.BankingSystem.Banking_System.dto.UpdateUserRequest;
import com.BankingSystem.Banking_System.dto.UserResponse;
import com.BankingSystem.Banking_System.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RequestMapping("/api/users")
@RestController
@Tag(
        name = "Users",
        description = "User registration, profile management and password operations"
)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(
            summary = "Create User",
            description = "Create user by taking input of firstName, lastName, email and password and autogenerate id and add creation time"
    )
    public UserResponse createUser(@RequestBody @Valid CreateUserRequest request){
        return userService.createUser(request);
    }

    @GetMapping
    public List<UserResponse> getAllUsers(){return userService.getAllUsers();}

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by ID",
            description = "Retrieves the authenticated user's profile by user ID"
    )
    public UserResponse getUserById(@PathVariable Long id){return userService.getUserById(id);}

    @PutMapping("/{id}")
    @Operation(
            summary = "Update user details",
            description = "Retrieves the authenticated user's profile by user ID and update the user details"
    )
    public UserResponse updateUserById(@PathVariable Long id,@Valid @RequestBody UpdateUserRequest request){return userService.updateUser(id, request);}

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete user by ID",
            description = "Retrieves the authenticated user's profile by user ID and delete the user"
    )
    public void deleteUserById(@PathVariable Long id){userService.deleteUserById(id);}

    @PutMapping("/password")
    @Operation(
            summary = "Change Password",
            description = "Retrieves the authenticated user's profile by user ID and changes the password"
    )
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(request);

        return ResponseEntity.noContent().build();
    }



}
