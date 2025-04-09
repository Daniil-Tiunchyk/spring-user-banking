package com.example.spring_user_banking.dao;

import com.example.spring_user_banking.model.Account;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountDao {
    Optional<Account> findByUserId(Long userId);

    /**
     * Пункт №4. Находим Account по userId, используя SELECT ... FOR UPDATE,
     * чтобы заблокировать строку в БД на время транзакции.
     */
    Optional<Account> findByUserIdForUpdate(Long userId);

    boolean updateBalance(Long userId, BigDecimal newBalance);
}
