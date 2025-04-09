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
     * Использует транзакцию + пессимистическую блокировку.
     */
    @Transactional
    public void transferMoney(Long fromUserId, Long toUserId, BigDecimal amount) {

        // 1) Валидация входных данных.
        if (fromUserId == null || toUserId == null) {
            throw new IllegalArgumentException("Отсутствует userId отправителя или получателя.");
        }
        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("Нельзя переводить самому себе!");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма перевода должна быть положительной!");
        }

        // 2) Блокируем аккаунты в упорядоченном порядке (чтобы избежать дедлоков).
        Account fromAcc;
        Account toAcc;
        if (fromUserId < toUserId) {
            fromAcc = accountDao.findByUserIdForUpdate(fromUserId)
                    .orElseThrow(() -> new IllegalArgumentException("Счёт отправителя не найден: userId=" + fromUserId));
            toAcc = accountDao.findByUserIdForUpdate(toUserId)
                    .orElseThrow(() -> new IllegalArgumentException("Счёт получателя не найден: userId=" + toUserId));
        } else {
            // Если fromUserId > toUserId, то сначала блокируем toUserId, а затем fromUserId:
            toAcc = accountDao.findByUserIdForUpdate(toUserId)
                    .orElseThrow(() -> new IllegalArgumentException("Счёт получателя не найден: userId=" + toUserId));
            fromAcc = accountDao.findByUserIdForUpdate(fromUserId)
                    .orElseThrow(() -> new IllegalArgumentException("Счёт отправителя не найден: userId=" + fromUserId));
        }

        // 3) Проверка баланса
        if (fromAcc.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Недостаточно средств для перевода!");
        }

        // 4) Пересчитываем и сохраняем
        BigDecimal newFromBalance = fromAcc.getBalance().subtract(amount);
        BigDecimal newToBalance = toAcc.getBalance().add(amount);

        boolean updatedFrom = accountDao.updateBalance(fromAcc.getUserId(), newFromBalance);
        boolean updatedTo = accountDao.updateBalance(toAcc.getUserId(), newToBalance);

        if (!updatedFrom || !updatedTo) {
            throw new RuntimeException("Ошибка при обновлении баланса (DAO вернуло false).");
        }

        log.info("Успешно перевели {} руб. от userId={} к userId={}.", amount, fromUserId, toUserId);
    }
}
