package com.example.spring_user_banking.dao;

import com.example.spring_user_banking.model.PhoneData;

import java.util.List;

public interface PhoneDataDao {
    List<PhoneData> findByUserId(Long userId);

    boolean save(PhoneData phoneData);
}
