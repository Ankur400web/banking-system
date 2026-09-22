package com.BankingSystem.Banking_System.RepoTest;

import com.BankingSystem.Banking_System.entity.Account;
import com.BankingSystem.Banking_System.entity.User;
import com.BankingSystem.Banking_System.repository.AccountRepository;
import com.BankingSystem.Banking_System.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByAccountNumber_shouldReturnAccount() {

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("accounttest@example.com");
        user.setPassword("Password123");

        User savedUser = userRepository.save(user);

        Account account = new Account();
        account.setAccountNumber("ACC10001");
        account.setAccountType("SAVINGS");
        account.setBalance(new BigDecimal("1000.00"));
        account.setUser(savedUser);

        Account savedAccount = accountRepository.save(account);

        Optional<Account> result =
                accountRepository.findByAccountNumber("ACC10001");

        assertTrue(result.isPresent());
        assertEquals(
                savedAccount.getAccountNumber(),
                result.get().getAccountNumber()
        );
    }

    @Test
    void findByAccountNumber_shouldReturnEmptyWhenAccountDoesNotExist() {

        Optional<Account> result =
                accountRepository.findByAccountNumber("UNKNOWN");

        assertTrue(result.isEmpty());
    }

    @Test
    void findById_shouldReturnAccount() {

        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("accountidtest@example.com");
        user.setPassword("Password123");

        User savedUser = userRepository.save(user);

        Account account = new Account();
        account.setAccountNumber("ACC10002");
        account.setAccountType("CURRENT");
        account.setBalance(new BigDecimal("2000.00"));
        account.setUser(savedUser);

        Account savedAccount = accountRepository.save(account);

        Optional<Account> result =
                accountRepository.findById(savedAccount.getId());

        assertTrue(result.isPresent());
        assertEquals(
                savedAccount.getId(),
                result.get().getId()
        );
    }

    @Test
    void findById_shouldReturnEmptyWhenAccountDoesNotExist() {

        Optional<Account> result =
                accountRepository.findById(999999L);

        assertFalse(result.isPresent());
    }
}