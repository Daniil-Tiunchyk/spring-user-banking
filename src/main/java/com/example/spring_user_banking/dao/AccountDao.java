// AccountDao.java
package com.example.spring_user_banking.dao;

import com.example.spring_user_banking.model.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountDao {
    /**
     * Ищет account по userId. (Без блокировки, обычный SELECT)
     */
    Optional<Account> findByUserId(Long userId);

    /**
     * Пункт №4. Находим Account по userId, используя SELECT ... FOR UPDATE,
     * чтобы заблокировать строку в БД на время транзакции.
     */
    Optional<Account> findByUserIdForUpdate(Long userId);

    /**
     * Возвращает все записи account с блокировкой FOR UPDATE.
     */
    List<Account> findAllForUpdate();

    /**
     * Обновляет баланс.
     */
    boolean updateBalance(Long userId, BigDecimal newBalance);
}
