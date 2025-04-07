package com.example.spring_user_banking.dao;


import com.example.spring_user_banking.model.Account;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountDao {
    Optional<Account> findByUserId(Long userId);

    boolean updateBalance(Long userId, BigDecimal newBalance);
}
