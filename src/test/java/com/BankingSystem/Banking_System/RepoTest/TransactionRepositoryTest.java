package com.BankingSystem.Banking_System.RepoTest;

import com.BankingSystem.Banking_System.entity.Account;
import com.BankingSystem.Banking_System.entity.Transaction;
import com.BankingSystem.Banking_System.entity.User;
import com.BankingSystem.Banking_System.enums.TransactionTypes;
import com.BankingSystem.Banking_System.repository.AccountRepository;
import com.BankingSystem.Banking_System.repository.TransactionRepository;
import com.BankingSystem.Banking_System.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByAccount_shouldReturnTransactions() {

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("transactiontest@example.com");
        user.setPassword("Password123");

        User savedUser = userRepository.save(user);

        Account account = new Account();
        account.setAccountNumber("ACC20001");
        account.setAccountType("SAVINGS");
        account.setBalance(new BigDecimal("1000.00"));
        account.setUser(savedUser);

        Account savedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setType(TransactionTypes.DEPOSIT);
        transaction.setAmount(new BigDecimal("500.00"));
        transaction.setBalanceAfter(new BigDecimal("1500.00"));
        transaction.setAccount(savedAccount);

        transactionRepository.save(transaction);

        List<Transaction> result =
                transactionRepository.findAllByAccount(savedAccount);

        assertEquals(1, result.size());
        assertEquals(
                new BigDecimal("500.00"),
                result.get(0).getAmount()
        );
    }

    @Test
    void findByAccount_shouldReturnEmptyListWhenNoTransactionsExist() {

        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("notransaction@example.com");
        user.setPassword("Password123");

        User savedUser = userRepository.save(user);

        Account account = new Account();
        account.setAccountNumber("ACC20002");
        account.setAccountType("SAVINGS");
        account.setBalance(new BigDecimal("1000.00"));
        account.setUser(savedUser);

        Account savedAccount = accountRepository.save(account);

        List<Transaction> result =
                transactionRepository.findAllByAccount(savedAccount);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByAccount_shouldReturnMultipleTransactions() {

        User user = new User();
        user.setFirstName("Mike");
        user.setLastName("Doe");
        user.setEmail("multipletransaction@example.com");
        user.setPassword("Password123");

        User savedUser = userRepository.save(user);

        Account account = new Account();
        account.setAccountNumber("ACC20003");
        account.setAccountType("CURRENT");
        account.setBalance(new BigDecimal("2000.00"));
        account.setUser(savedUser);

        Account savedAccount = accountRepository.save(account);

        Transaction deposit = new Transaction();
        deposit.setType(TransactionTypes.DEPOSIT);
        deposit.setAmount(new BigDecimal("500.00"));
        deposit.setBalanceAfter(new BigDecimal("2500.00"));
        deposit.setAccount(savedAccount);

        Transaction withdraw = new Transaction();
        withdraw.setType(TransactionTypes.WITHDRAWAL);
        withdraw.setAmount(new BigDecimal("200.00"));
        withdraw.setBalanceAfter(new BigDecimal("2300.00"));
        withdraw.setAccount(savedAccount);

        transactionRepository.save(deposit);
        transactionRepository.save(withdraw);

        List<Transaction> result =
                transactionRepository.findAllByAccount(savedAccount);

        assertEquals(2, result.size());
    }

    @Test
    void findByAccount_shouldReturnOnlyTransactionsForRequestedAccount() {

        User user = new User();
        user.setFirstName("Alex");
        user.setLastName("Doe");
        user.setEmail("correctaccount@example.com");
        user.setPassword("Password123");

        User savedUser = userRepository.save(user);

        Account account1 = new Account();
        account1.setAccountNumber("ACC20004");
        account1.setAccountType("SAVINGS");
        account1.setBalance(new BigDecimal("1000.00"));
        account1.setUser(savedUser);

        Account account2 = new Account();
        account2.setAccountNumber("ACC20005");
        account2.setAccountType("CURRENT");
        account2.setBalance(new BigDecimal("2000.00"));
        account2.setUser(savedUser);

        Account savedAccount1 = accountRepository.save(account1);
        Account savedAccount2 = accountRepository.save(account2);

        Transaction transaction1 = new Transaction();
        transaction1.setType(TransactionTypes.DEPOSIT);
        transaction1.setAmount(new BigDecimal("500.00"));
        transaction1.setBalanceAfter(new BigDecimal("1500.00"));
        transaction1.setAccount(savedAccount1);

        Transaction transaction2 = new Transaction();
        transaction2.setType(TransactionTypes.WITHDRAWAL);
        transaction2.setAmount(new BigDecimal("300.00"));
        transaction2.setBalanceAfter(new BigDecimal("1700.00"));
        transaction2.setAccount(savedAccount2);

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);

        List<Transaction> result =
                transactionRepository.findAllByAccount(savedAccount1);

        assertEquals(1, result.size());
        assertEquals(
                "ACC20004",
                result.get(0).getAccount().getAccountNumber()
        );
    }


}
