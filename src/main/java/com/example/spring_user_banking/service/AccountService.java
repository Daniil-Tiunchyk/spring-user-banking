package com.example.spring_user_banking.service;

import com.example.spring_user_banking.dao.AccountDao;
import com.example.spring_user_banking.model.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountDao accountDao;

    /**
     * Получение счёта по userId.
     */
    public Optional<Account> getAccountByUserId(Long userId) {
        return accountDao.findByUserId(userId);
    }

    /**
     * Обновление баланса.
     */
    public boolean updateBalance(Long userId, BigDecimal newBalance) {
        return accountDao.updateBalance(userId, newBalance);
    }
}
