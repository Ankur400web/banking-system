package com.BankingSystem.Banking_System.service;

import com.BankingSystem.Banking_System.dto.CreateUserRequest;
import com.BankingSystem.Banking_System.dto.UserResponse;
import com.BankingSystem.Banking_System.repository.UserRepository;
import com.BankingSystem.Banking_System.entity.User;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

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

    public List<UserResponse> getAllUsers(){

        List<User> users = userRepository.findAll();

        if ((users.isEmpty())){
            throw new RuntimeException("No User Exist");
        }

        List<UserResponse> userResponsesList = new ArrayList<>();

        for (User u:users){
            UserResponse userResponse = new UserResponse();

            userResponse.setId(u.getId());
            userResponse.setFirstName(u.getFirstName());
            userResponse.setLastName(u.getLastName());
            userResponse.setEmail(u.getEmail());
            userResponse.setCreatedAt(u.getCreatedAt());
            userResponsesList.add(userResponse);
        }

        return userResponsesList;

    }

    public UserResponse getUserById(Long id) {

        User user = userRepository.findUserById(id);

        if (user == null) {
            throw new RuntimeException("User doesn't exist");
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setId(user.getId());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setCreatedAt(user.getCreatedAt());

        return userResponse;
    }

    public UserResponse updateUser(Long id, CreateUserRequest request){
        User user = userRepository.findUserById(id);
        if(user==null){
            throw new RuntimeException("User doesn't exist");
        }


        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        userRepository.save(user);

        UserResponse userResponse = new UserResponse();

        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setId(user.getId());
        userResponse.setCreatedAt(user.getCreatedAt());

        return userResponse;

    }

    public void deleteUserById(Long id){
        User user = userRepository.findUserById(id);
        if (user==null){
            throw new RuntimeException("User doesn't exist");
        }

        userRepository.delete(user);
    }
}
