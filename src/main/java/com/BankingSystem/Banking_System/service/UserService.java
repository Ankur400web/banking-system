package com.BankingSystem.Banking_System.service;

import com.BankingSystem.Banking_System.dto.CreateUserRequest;
import com.BankingSystem.Banking_System.dto.UserResponse;
import com.BankingSystem.Banking_System.repository.UserRepository;
import com.BankingSystem.Banking_System.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(CreateUserRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already Exist");
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        User savedUser = userRepository.save(user);

        UserResponse userResponse = new UserResponse();

        userResponse.setId(savedUser.getId());
        userResponse.setFirstName(savedUser.getFirstName());
        userResponse.setLastName(savedUser.getLastName());
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setCreatedAt(savedUser.getCreatedAt());

        return userResponse;
    }
}
