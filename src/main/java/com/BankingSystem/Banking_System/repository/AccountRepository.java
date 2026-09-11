package com.BankingSystem.Banking_System.repository;

import com.BankingSystem.Banking_System.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accNum);

    boolean existsByAccountNumber(String accNum);


}
