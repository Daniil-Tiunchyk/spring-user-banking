package com.example.spring_user_banking.service;

import com.example.spring_user_banking.dao.AccountDao;
import com.example.spring_user_banking.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferServiceTest {

    @Mock
    private AccountDao accountDao;

    private TransferService transferService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Создаем сервис с замоканным DAO
        transferService = new TransferService(accountDao);
    }

    @Test
    @DisplayName("Успешный перевод денег, достаточный баланс, разные пользователи")
    void testTransferMoneySuccess() {
        Long fromUserId = 1L;
        Long toUserId = 2L;
        BigDecimal amount = new BigDecimal("500.00");

        // Аккаунт отправителя
        Account fromAcc = Account.builder()
                .userId(fromUserId)
                .balance(new BigDecimal("1500.00"))
                .build();
        // Аккаунт получателя
        Account toAcc = Account.builder()
                .userId(toUserId)
                .balance(new BigDecimal("300.00"))
                .build();

        // Мокаем методы DAO
        when(accountDao.findByUserIdForUpdate(fromUserId)).thenReturn(Optional.of(fromAcc));
        when(accountDao.findByUserIdForUpdate(toUserId)).thenReturn(Optional.of(toAcc));
        when(accountDao.updateBalance(eq(fromUserId), any())).thenReturn(true);
        when(accountDao.updateBalance(eq(toUserId), any())).thenReturn(true);

        // Вызываем метод
        transferService.transferMoney(fromUserId, toUserId, amount);

        // Проверяем, что балансы в итоге уменьшились/увеличились
        BigDecimal expectedFromBalance = new BigDecimal("1000.00"); // 1500 - 500
        BigDecimal expectedToBalance = new BigDecimal("800.00");    // 300 + 500

        verify(accountDao).updateBalance(fromUserId, expectedFromBalance);
        verify(accountDao).updateBalance(toUserId, expectedToBalance);
    }

    @Test
    @DisplayName("Ошибка: перевод самому себе должен вызывать IllegalArgumentException")
    void testTransferMoneyToSelf() {
        Long fromUserId = 1L;
        Long toUserId = 1L;
        BigDecimal amount = new BigDecimal("100.00");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> transferService.transferMoney(fromUserId, toUserId, amount));
        assertEquals("Нельзя переводить самому себе!", ex.getMessage());
    }

    @Test
    @DisplayName("Ошибка: null в fromUserId или toUserId")
    void testTransferMoneyWithNullUser() {
        Long fromUserId = null;
        Long toUserId = 2L;
        BigDecimal amount = new BigDecimal("100.00");

        assertThrows(IllegalArgumentException.class,
                () -> transferService.transferMoney(fromUserId, toUserId, amount));
    }

    @Test
    @DisplayName("Ошибка: недостаточно средств на счёте отправителя")
    void testTransferMoneyInsufficientFunds() {
        Long fromUserId = 1L;
        Long toUserId = 2L;
        BigDecimal amount = new BigDecimal("2000.00");

        // Аккаунт отправителя — баланс 1500, хотим перевести 2000
        Account fromAcc = Account.builder()
                .userId(fromUserId)
                .balance(new BigDecimal("1500.00"))
                .build();
        // Аккаунт получателя
        Account toAcc = Account.builder()
                .userId(toUserId)
                .balance(new BigDecimal("500.00"))
                .build();

        when(accountDao.findByUserIdForUpdate(fromUserId)).thenReturn(Optional.of(fromAcc));
        when(accountDao.findByUserIdForUpdate(toUserId)).thenReturn(Optional.of(toAcc));

        // Ожидаем исключение
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> transferService.transferMoney(fromUserId, toUserId, amount));
        assertEquals("Недостаточно средств для перевода!", ex.getMessage());

        // Убеждаемся, что updateBalance даже не вызывался
        verify(accountDao, never()).updateBalance(anyLong(), any());
    }

    @Test
    @DisplayName("Ошибка: сумма перевода <= 0")
    void testTransferMoneyNonPositiveAmount() {
        Long fromUserId = 1L;
        Long toUserId = 2L;

        assertThrows(IllegalArgumentException.class,
                () -> transferService.transferMoney(fromUserId, toUserId, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> transferService.transferMoney(fromUserId, toUserId, new BigDecimal("-1.00")));
    }

    @Test
    @DisplayName("Ошибка: счёт отправителя не найден")
    void testTransferMoneyAccountFromNotFound() {
        Long fromUserId = 1L;
        Long toUserId = 2L;
        BigDecimal amount = new BigDecimal("100.00");

        // Возвращаем Optional.empty()
        when(accountDao.findByUserIdForUpdate(fromUserId)).thenReturn(Optional.empty());

        // Проверяем, что выбрасывается исключение
        assertThrows(IllegalArgumentException.class,
                () -> transferService.transferMoney(fromUserId, toUserId, amount));
    }

    @Test
    @DisplayName("Ошибка: счёт получателя не найден")
    void testTransferMoneyAccountToNotFound() {
        Long fromUserId = 1L;
        Long toUserId = 2L;
        BigDecimal amount = new BigDecimal("100.00");

        // fromAcc есть, toAcc пуст
        Account fromAcc = Account.builder()
                .userId(fromUserId)
                .balance(new BigDecimal("1500.00"))
                .build();

        when(accountDao.findByUserIdForUpdate(fromUserId)).thenReturn(Optional.of(fromAcc));
        when(accountDao.findByUserIdForUpdate(toUserId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> transferService.transferMoney(fromUserId, toUserId, amount));
    }

    @Test
    @DisplayName("Ошибка: updateBalance в DAO вернул false")
    void testTransferMoneyDaoUpdateFailed() {
        Long fromUserId = 1L;
        Long toUserId = 2L;
        BigDecimal amount = new BigDecimal("100.00");

        Account fromAcc = Account.builder().userId(fromUserId).balance(new BigDecimal("1500.00")).build();
        Account toAcc = Account.builder().userId(toUserId).balance(new BigDecimal("100.00")).build();

        when(accountDao.findByUserIdForUpdate(fromUserId)).thenReturn(Optional.of(fromAcc));
        when(accountDao.findByUserIdForUpdate(toUserId)).thenReturn(Optional.of(toAcc));

        // Пусть для отправителя всё норм, а для получателя DAO обновить не сможет
        when(accountDao.updateBalance(fromUserId, new BigDecimal("1400.00"))).thenReturn(true);
        when(accountDao.updateBalance(toUserId, new BigDecimal("200.00"))).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> transferService.transferMoney(fromUserId, toUserId, amount));
    }
}
