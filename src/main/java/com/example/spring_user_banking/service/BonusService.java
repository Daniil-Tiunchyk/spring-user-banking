// BonusService.java
package com.example.spring_user_banking.service;

import com.example.spring_user_banking.dao.AccountDao;
import com.example.spring_user_banking.model.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Сервис для периодического начисления "бонуса" +10% к balance
 * (не более 2.07× от "начального" баланса, который храним в Redis).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BonusService {

    private final AccountDao accountDao;
    private final RedisTemplate<String, String> redisTemplate;

    private static final BigDecimal GROWTH_RATE = new BigDecimal("0.10");  // +10%
    private static final BigDecimal MAX_MULTIPLIER = new BigDecimal("2.07");

    // Раз в 30 секунд
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void applyBonusInterest() {
        // 1) Список всех аккаунтов, "FOR UPDATE" => гарантированная блокировка на время транзакции
        List<Account> allAccounts = accountDao.findAllForUpdate();
        log.info("applyBonusInterest() locked {} accounts via FOR UPDATE", allAccounts.size());

        // 2) Идём по каждому аккаунту
        for (Account acc : allAccounts) {
            BigDecimal currentBalance = acc.getBalance();
            BigDecimal initialBalance = getInitialBalanceFromRedis(acc.getUserId(), currentBalance);
            BigDecimal maxAllowed = initialBalance.multiply(MAX_MULTIPLIER);

            // Если уже достигнут "потолок"
            if (currentBalance.compareTo(maxAllowed) >= 0) {
                continue;
            }

            // Считаем баланс = balance * 1.1
            BigDecimal newBalance = currentBalance.multiply(BigDecimal.ONE.add(GROWTH_RATE));
            // Но не больше maxAllowed
            if (newBalance.compareTo(maxAllowed) > 0) {
                newBalance = maxAllowed;
            }

            boolean updated = accountDao.updateBalance(acc.getUserId(), newBalance);
            if (!updated) {
                throw new IllegalStateException("Failed to update balance for userId=" + acc.getUserId());
            }
        }

        log.info("Планировщик applyBonusInterest() завершён, обработано {} аккаунтов.", allAccounts.size());
    }

    /**
     * Берём "initialBalance" из Redis,
     * если там ничего нет — записываем текущее (стартовое) значение и возвращаем его.
     */
    private BigDecimal getInitialBalanceFromRedis(Long userId, BigDecimal currentBalance) {
        String key = "initialBalance:" + userId;
        String storedVal = redisTemplate.opsForValue().get(key);
        if (storedVal == null) {
            // Записываем, что "start" = currentBalance
            redisTemplate.opsForValue().set(key, currentBalance.toPlainString());
            return currentBalance;
        } else {
            return new BigDecimal(storedVal);
        }
    }
}
