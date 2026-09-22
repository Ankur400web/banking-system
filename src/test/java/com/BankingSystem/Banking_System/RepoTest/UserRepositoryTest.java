package com.BankingSystem.Banking_System.RepoTest;

import com.BankingSystem.Banking_System.entity.User;
import com.BankingSystem.Banking_System.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_shouldReturnUser() {

        User user = new User();

        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPassword("Password123");

        User savedUser = userRepository.save(user);

        Optional<User> result =
                userRepository.findByEmail("john@example.com");

        assertTrue(result.isPresent());
        assertEquals(savedUser.getEmail(), result.get().getEmail());
    }

    @Test
    void findByEmail_shouldReturnEmptyWhenUserDoesNotExist() {

        Optional<User> result =
                userRepository.findByEmail("unknown@example.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void existsByEmail_shouldReturnTrueWhenEmailExists() {

        User user = new User();

        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("exists@example.com");
        user.setPassword("Password123");

        userRepository.save(user);

        boolean result =
                userRepository.existsByEmail("exists@example.com");

        assertTrue(result);
    }

    @Test
    void existsByEmail_shouldReturnFalseWhenEmailDoesNotExist() {

        boolean result =
                userRepository.existsByEmail("unknown@example.com");

        assertFalse(result);
    }

}

