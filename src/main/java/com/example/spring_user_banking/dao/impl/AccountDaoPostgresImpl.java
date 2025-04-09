package com.example.spring_user_banking.dao.impl;

import com.example.spring_user_banking.dao.AccountDao;
import com.example.spring_user_banking.exception.CustomException;
import com.example.spring_user_banking.model.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static com.example.spring_user_banking.config.database.DatabaseConstants.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AccountDaoPostgresImpl implements AccountDao {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Account> ACCOUNT_ROW_MAPPER = (rs, rowNum) ->
            Account.builder()
                    .id(rs.getLong(ID_COLUMN))
                    .userId(rs.getLong(USER_ID_COLUMN))
                    .balance(rs.getBigDecimal(BALANCE_COLUMN))
                    .build();

    @Override
    public Optional<Account> findByUserId(final Long userId) {
        final String sql = String.format("SELECT %s, %s, %s FROM %s WHERE %s = ?",
                ID_COLUMN, USER_ID_COLUMN, BALANCE_COLUMN, ACCOUNT_TABLE, USER_ID_COLUMN);

        try {
            Account account = jdbcTemplate.queryForObject(sql, ACCOUNT_ROW_MAPPER, userId);
            return Optional.ofNullable(account);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Счет не найден для userId={}", userId);
            return Optional.empty();
        } catch (DataAccessException e) {
            log.error("Ошибка поиска счета для userId={}", userId, e);
            throw new CustomException("Ошибка получения счета", e);
        }
    }

    @Override
    public Optional<Account> findByUserIdForUpdate(final Long userId) {
        final String sql = String.format(
                "SELECT %s, %s, %s FROM %s WHERE %s = ? FOR UPDATE",
                ID_COLUMN, USER_ID_COLUMN, BALANCE_COLUMN, ACCOUNT_TABLE, USER_ID_COLUMN
        );
        try {
            Account acc = jdbcTemplate.queryForObject(sql, ACCOUNT_ROW_MAPPER, userId);
            return Optional.ofNullable(acc);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Счет не найден для userId={} при использовании FOR UPDATE", userId);
            return Optional.empty();
        } catch (DataAccessException e) {
            log.error("Ошибка поиска счета для userId={} при использовании FOR UPDATE", userId, e);
            throw new CustomException("Ошибка получения счета с FOR UPDATE", e);
        }
    }

    @Override
    @Transactional
    public boolean updateBalance(final Long userId, final BigDecimal newBalance) {
        final String sql = String.format("UPDATE %s SET %s = ? WHERE %s = ?",
                ACCOUNT_TABLE, BALANCE_COLUMN, USER_ID_COLUMN);
        try {
            int rowsAffected = jdbcTemplate.update(sql, newBalance, userId);
            return rowsAffected == AFFECTED_ROWS_ONE;
        } catch (DataAccessException e) {
            log.error("Ошибка обновления баланса для userId={}, баланс={}", userId, newBalance, e);
            throw new CustomException("Ошибка обновления баланса", e);
        }
    }
}
