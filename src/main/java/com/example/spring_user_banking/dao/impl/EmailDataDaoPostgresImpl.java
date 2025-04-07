package com.example.spring_user_banking.dao.impl;

import com.example.spring_user_banking.dao.EmailDataDao;
import com.example.spring_user_banking.exception.CustomException;
import com.example.spring_user_banking.model.EmailData;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EmailDataDaoPostgresImpl implements EmailDataDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<EmailData> rowMapper = (rs, rowNum) -> {
        EmailData e = new EmailData();
        e.setId(rs.getLong("id"));
        e.setUserId(rs.getLong("user_id"));
        e.setEmail(rs.getString("email"));
        return e;
    };

    @Override
    public List<EmailData> findByUserId(Long userId) {
        try {
            String sql = "SELECT * FROM email_data WHERE user_id = ?";
            return jdbcTemplate.query(sql, rowMapper, userId);
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        } catch (Exception e) {
            throw new CustomException("Error retrieving email data for userId: " + userId);
        }
    }

    @Override
    public boolean save(EmailData emailData) {
        try {
            String sql = "INSERT INTO email_data (user_id, email) VALUES (?, ?)";
            return jdbcTemplate.update(sql, emailData.getUserId(), emailData.getEmail()) == 1;
        } catch (DuplicateKeyException e) {
            throw new CustomException("Email already exists: " + emailData.getEmail());
        } catch (Exception e) {
            throw new CustomException("Error saving email data for userId: " + emailData.getUserId());
        }
    }

    @Override
    public boolean deleteByUserIdAndEmail(Long userId, String email) {
        try {
            String sql = "DELETE FROM email_data WHERE user_id = ? AND email = ?";
            return jdbcTemplate.update(sql, userId, email) == 1;
        } catch (Exception e) {
            throw new CustomException("Error deleting email: " + email);
        }
    }
}
