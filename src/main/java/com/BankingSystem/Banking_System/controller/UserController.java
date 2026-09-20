package com.BankingSystem.Banking_System.controller;

import com.BankingSystem.Banking_System.dto.ChangePasswordRequest;
import com.BankingSystem.Banking_System.dto.CreateUserRequest;
import com.BankingSystem.Banking_System.dto.UpdateUserRequest;
import com.BankingSystem.Banking_System.dto.UserResponse;
import com.BankingSystem.Banking_System.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(
        name = "Users",
        description = "User registration, profile management and password operations"
)
@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse createUser(@RequestBody @Valid CreateUserRequest request){
        return userService.createUser(request);
    }

    @GetMapping
    public List<UserResponse> getAllUsers(){return userService.getAllUsers();}

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id){return userService.getUserById(id);}

    @PutMapping("/{id}")
    public UserResponse updateUserById(@PathVariable Long id, @RequestBody UpdateUserRequest request){return userService.updateUser(id, request);}

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id){userService.deleteUserById(id);}

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(request);

        return ResponseEntity.noContent().build();
    }


}
