package com.example.spring_user_banking.scheduler;

import com.example.spring_user_banking.service.BonusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BalanceUpdateScheduler {

    private final BonusService bonusService;

    /**
     * Запускается каждые 30 секунд.
     * Просто делегирует вызов в BonusService, где вся основная логика.
     */
    @Scheduled(fixedRate = 30000)
    public void runBalanceUpdates() {
        log.info("Запуск планировщика runBalanceUpdates() ...");
        bonusService.applyBonusInterest();
    }
}
