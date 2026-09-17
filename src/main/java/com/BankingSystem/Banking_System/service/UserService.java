package com.BankingSystem.Banking_System.service;

import com.BankingSystem.Banking_System.dto.CreateUserRequest;
import com.BankingSystem.Banking_System.dto.LoginRequest;
import com.BankingSystem.Banking_System.dto.LoginResponse;
import com.BankingSystem.Banking_System.dto.UserResponse;
import com.BankingSystem.Banking_System.exception.DuplicateEmailException;
import com.BankingSystem.Banking_System.exception.InvalidCredentialsException;
import com.BankingSystem.Banking_System.exception.UnauthorizedAccountAccessException;
import com.BankingSystem.Banking_System.exception.UserNotFoundException;
import com.BankingSystem.Banking_System.repository.UserRepository;
import com.BankingSystem.Banking_System.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;




import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }



    public UserResponse createUser(CreateUserRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateEmailException("Email Already Exist");
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

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
            throw new UserNotFoundException("User doesn't exist");
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

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User doesn't exist"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User authenticatedUser = (User) authentication.getPrincipal();

        if (!authenticatedUser.getId().equals(id)){
            throw new UnauthorizedAccountAccessException("You are not authorized");
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
        User user = userRepository.findById(id)
            .orElseThrow(()-> new UserNotFoundException("User doesn't exist"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User authenticatedUser = (User) authentication.getPrincipal();

        if (!authenticatedUser.getId().equals(id)){
            throw new UnauthorizedAccountAccessException("You are not authorized to modify other users");
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
            throw new UserNotFoundException("User doesn't exist");
        }

        userRepository.delete(user);
    }

    public LoginResponse userLogin(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Invalid email or password");
        }




        LoginResponse response = new LoginResponse();

        String token = jwtService.generateToken(user);

        response.setToken(token);
        response.setTokenType("Bearer");

        return response;


    }
}
