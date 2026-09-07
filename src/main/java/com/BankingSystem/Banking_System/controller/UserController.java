package com.BankingSystem.Banking_System.controller;

import com.BankingSystem.Banking_System.dto.CreateUserRequest;
import com.BankingSystem.Banking_System.dto.UserResponse;
import com.BankingSystem.Banking_System.service.UserService;
import org.springframework.web.bind.annotation.*;


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


}
