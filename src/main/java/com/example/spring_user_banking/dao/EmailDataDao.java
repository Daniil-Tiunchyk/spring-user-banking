package com.example.spring_user_banking.dao;

import com.example.spring_user_banking.model.EmailData;

import java.util.List;

public interface EmailDataDao {
    List<EmailData> findByUserId(Long userId);

    boolean save(EmailData emailData);

    boolean deleteByUserIdAndEmail(Long userId, String email);
}
