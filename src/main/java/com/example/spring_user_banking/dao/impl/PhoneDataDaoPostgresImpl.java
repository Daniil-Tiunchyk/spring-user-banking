package com.example.spring_user_banking.dao.impl;

import com.example.spring_user_banking.dao.PhoneDataDao;
import com.example.spring_user_banking.exception.CustomException;
import com.example.spring_user_banking.model.PhoneData;
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
public class PhoneDataDaoPostgresImpl implements PhoneDataDao {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<PhoneData> PHONE_ROW_MAPPER = (rs, rowNum) ->
            PhoneData.builder()
                    .id(rs.getLong(ID_COLUMN))
                    .userId(rs.getLong(USER_ID_COLUMN))
                    .phone(rs.getString(PHONE_COLUMN))
                    .build();

    @Override
    public List<PhoneData> findByUserId(final Long userId) {
        final String sql = String.format("SELECT %s, %s, %s FROM %s WHERE %s = ?",
                ID_COLUMN, USER_ID_COLUMN, PHONE_COLUMN, PHONE_DATA_TABLE, USER_ID_COLUMN);
        try {
            return jdbcTemplate.query(sql, PHONE_ROW_MAPPER, userId);
        } catch (DataAccessException e) {
            log.error("Error retrieving phones for userId={}", userId, e);
            throw new CustomException("Error retrieving phones", e);
        }
    }

    @Override
    @Transactional
    public boolean save(final PhoneData phoneData) {
        final String sql = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?)",
                PHONE_DATA_TABLE, USER_ID_COLUMN, PHONE_COLUMN);
        try {
            int rowsAffected = jdbcTemplate.update(sql, phoneData.getUserId(), phoneData.getPhone());
            return rowsAffected == AFFECTED_ROWS_ONE;
        } catch (DuplicateKeyException e) {
            log.warn("Duplicate phone={} for userId={}", phoneData.getPhone(), phoneData.getUserId());
            throw new CustomException("Phone already exists", e);
        } catch (DataAccessException e) {
            log.error("Error saving phone={} for userId={}", phoneData.getPhone(), phoneData.getUserId(), e);
            throw new CustomException("Error saving phone", e);
        }
    }

    @Override
    @Transactional
    public boolean deleteByUserIdAndPhone(final Long userId, final String phone) {
        final String sql = String.format("DELETE FROM %s WHERE %s = ? AND %s = ?",
                PHONE_DATA_TABLE, USER_ID_COLUMN, PHONE_COLUMN);
        try {
            int rowsAffected = jdbcTemplate.update(sql, userId, phone);
            return rowsAffected == AFFECTED_ROWS_ONE;
        } catch (DataAccessException e) {
            log.error("Error deleting phone={} for userId={}", phone, userId, e);
            throw new CustomException("Error deleting phone", e);
        }
    }
}
