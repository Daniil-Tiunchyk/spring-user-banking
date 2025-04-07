package com.example.spring_user_banking.dao.impl;

import com.example.spring_user_banking.dao.PhoneDataDao;
import com.example.spring_user_banking.exception.CustomException;
import com.example.spring_user_banking.model.PhoneData;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PhoneDataDaoPostgresImpl implements PhoneDataDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<PhoneData> rowMapper = (rs, rowNum) -> {
        PhoneData p = new PhoneData();
        p.setId(rs.getLong("id"));
        p.setUserId(rs.getLong("user_id"));
        p.setPhone(rs.getString("phone"));
        return p;
    };

    @Override
    public List<PhoneData> findByUserId(Long userId) {
        try {
            String sql = "SELECT * FROM phone_data WHERE user_id = ?";
            return jdbcTemplate.query(sql, rowMapper, userId);
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        } catch (Exception e) {
            throw new CustomException("Error retrieving phone data for userId: " + userId);
        }
    }

    @Override
    public boolean save(PhoneData phoneData) {
        try {
            String sql = "INSERT INTO phone_data (user_id, phone) VALUES (?, ?)";
            return jdbcTemplate.update(sql, phoneData.getUserId(), phoneData.getPhone()) == 1;
        } catch (DuplicateKeyException e) {
            throw new CustomException("Phone already exists: " + phoneData.getPhone());
        } catch (Exception e) {
            throw new CustomException("Error saving phone data for userId: " + phoneData.getUserId());
        }
    }

    @Override
    public boolean deleteByUserIdAndPhone(Long userId, String phone) {
        try {
            String sql = "DELETE FROM phone_data WHERE user_id = ? AND phone = ?";
            return jdbcTemplate.update(sql, userId, phone) == 1;
        } catch (Exception e) {
            throw new CustomException("Error deleting phone: " + phone);
        }
    }
}
