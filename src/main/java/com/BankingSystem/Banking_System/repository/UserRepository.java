package com.BankingSystem.Banking_System.repository;


import com.BankingSystem.Banking_System.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    User findUserById(Long id);
}
