package com.BankingSystem.Banking_System.controller;


import com.BankingSystem.Banking_System.dto.LoginRequest;
import com.BankingSystem.Banking_System.dto.LoginResponse;
import com.BankingSystem.Banking_System.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
@Tag(
        name = "Authentication",
        description = "User authentication and JWT token generation"
)
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public LoginResponse userLogin(@Valid @RequestBody LoginRequest request){
        return userService.userLogin(request);
    }
}
