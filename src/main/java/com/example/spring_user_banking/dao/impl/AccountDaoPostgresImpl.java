package com.example.spring_user_banking.dao.impl;

import com.example.spring_user_banking.dao.AccountDao;
import com.example.spring_user_banking.exception.CustomException;
import com.example.spring_user_banking.model.Account;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AccountDaoPostgresImpl implements AccountDao {

    private static final Logger logger = LoggerFactory.getLogger(AccountDaoPostgresImpl.class);

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Account> accountRowMapper = (rs, rowNum) -> {
        Account a = new Account();
        a.setId(rs.getLong("id"));
        a.setUserId(rs.getLong("user_id"));
        a.setBalance(rs.getBigDecimal("balance"));
        return a;
    };

    @Override
    public Optional<Account> findByUserId(Long userId) {
        try {
            String sql = "SELECT * FROM account WHERE user_id = ?";
            Account account = jdbcTemplate.queryForObject(sql, accountRowMapper, userId);
            return Optional.ofNullable(account);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error findByUserId={}", userId, e);
            throw new CustomException("Error retrieving account for userId: " + userId);
        }
    }

    @Override
    public boolean updateBalance(Long userId, BigDecimal newBalance) {
        try {
            String sql = "UPDATE account SET balance = ? WHERE user_id = ?";
            int updated = jdbcTemplate.update(sql, newBalance, userId);
            return updated == 1;
        } catch (Exception e) {
            logger.error("Error updateBalance userId={}, newBalance={}", userId, newBalance, e);
            throw new CustomException("Error updating balance for userId: " + userId);
        }
    }
}
