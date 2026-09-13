package com.BankingSystem.Banking_System.repository;

import com.BankingSystem.Banking_System.entity.Account;
import com.BankingSystem.Banking_System.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {
        List<Transaction> findAllByAccount(Account account);

}
