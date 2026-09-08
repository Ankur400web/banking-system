package com.BankingSystem.Banking_System.controller;

import com.BankingSystem.Banking_System.dto.CreateUserRequest;
import com.BankingSystem.Banking_System.dto.UserResponse;
import com.BankingSystem.Banking_System.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse createUser(@RequestBody CreateUserRequest request){
        return userService.createUser(request);
    }

    @GetMapping
    public List<UserResponse> getAllUsers(){return userService.getAllUsers();}

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id){return userService.getUserById(id);}

    @PutMapping("/{id}")
    public UserResponse updateUserById(@PathVariable Long id, @RequestBody CreateUserRequest request){return userService.updateUser(id, request);}

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id){userService.deleteUserById(id);}


}
