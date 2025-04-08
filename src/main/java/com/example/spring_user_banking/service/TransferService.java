package com.example.spring_user_banking.service;

import com.example.spring_user_banking.dao.AccountDao;
import com.example.spring_user_banking.model.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {

    private final AccountDao accountDao;

    /**
     * Перевод денег от fromUserId к toUserId.
     */
    @Transactional
    public void transferMoney(Long fromUserId, Long toUserId, BigDecimal amount) {
        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("Нельзя переводить самому себе!");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть > 0.");
        }

        Account fromAcc = accountDao.findByUserId(fromUserId)
                .orElseThrow(() -> new IllegalArgumentException("Счёт отправителя не найден"));
        Account toAcc = accountDao.findByUserId(toUserId)
                .orElseThrow(() -> new IllegalArgumentException("Счёт получателя не найден"));

        if (fromAcc.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Недостаточно средств для перевода!");
        }

        BigDecimal newFromBalance = fromAcc.getBalance().subtract(amount);
        BigDecimal newToBalance = toAcc.getBalance().add(amount);

        boolean updateFrom = accountDao.updateBalance(fromUserId, newFromBalance);
        boolean updateTo = accountDao.updateBalance(toUserId, newToBalance);

        if (!updateFrom || !updateTo) {
            throw new RuntimeException("Ошибка при обновлении баланса (DAO вернуло false).");
        }
    }
}
