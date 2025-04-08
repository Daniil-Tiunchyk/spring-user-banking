package com.example.spring_user_banking.dao.impl;

import com.example.spring_user_banking.dao.EmailDataDao;
import com.example.spring_user_banking.exception.CustomException;
import com.example.spring_user_banking.model.EmailData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.spring_user_banking.config.database.DatabaseConstants.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EmailDataDaoPostgresImpl implements EmailDataDao {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<EmailData> EMAIL_ROW_MAPPER = (rs, rowNum) ->
            EmailData.builder()
                    .id(rs.getLong(ID_COLUMN))
                    .userId(rs.getLong(USER_ID_COLUMN))
                    .email(rs.getString(EMAIL_COLUMN))
                    .build();

    @Override
    public List<EmailData> findByUserId(final Long userId) {
        final String sql = String.format("SELECT %s, %s, %s FROM %s WHERE %s = ?",
                ID_COLUMN, USER_ID_COLUMN, EMAIL_COLUMN, EMAIL_DATA_TABLE, USER_ID_COLUMN);
        try {
            return jdbcTemplate.query(sql, EMAIL_ROW_MAPPER, userId);
        } catch (DataAccessException e) {
            log.error("Error retrieving emails for userId={}", userId, e);
            throw new CustomException("Error retrieving emails", e);
        }
    }

    @Override
    @Transactional
    public boolean save(final EmailData emailData) {
        final String sql = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?)",
                EMAIL_DATA_TABLE, USER_ID_COLUMN, EMAIL_COLUMN);
        try {
            int rowsAffected = jdbcTemplate.update(sql, emailData.getUserId(), emailData.getEmail());
            return rowsAffected == AFFECTED_ROWS_ONE;
        } catch (DuplicateKeyException e) {
            log.warn("Duplicate email={} for userId={}", emailData.getEmail(), emailData.getUserId());
            throw new CustomException("Email already exists", e);
        } catch (DataAccessException e) {
            log.error("Error saving email={} for userId={}", emailData.getEmail(), emailData.getUserId(), e);
            throw new CustomException("Error saving email", e);
        }
    }

    @Override
    @Transactional
    public boolean deleteByUserIdAndEmail(final Long userId, final String email) {
        final String sql = String.format("DELETE FROM %s WHERE %s = ? AND %s = ?",
                EMAIL_DATA_TABLE, USER_ID_COLUMN, EMAIL_COLUMN);
        try {
            int rowsAffected = jdbcTemplate.update(sql, userId, email);
            return rowsAffected == AFFECTED_ROWS_ONE;
        } catch (DataAccessException e) {
            log.error("Error deleting email={} for userId={}", email, userId, e);
            throw new CustomException("Error deleting email", e);
        }
    }
}
