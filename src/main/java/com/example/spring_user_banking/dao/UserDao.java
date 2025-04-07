package com.example.spring_user_banking.dao;

import com.example.spring_user_banking.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findById(Long id);
    List<User> findAll();
    boolean update(User user);

    boolean addEmail(Long userId, String email);
    boolean removeEmail(Long userId, String email);
    boolean addPhone(Long userId, String phone);
    boolean removePhone(Long userId, String phone);

    List<User> findByNameStartingWith(String namePrefix);
    List<User> findByBirthDateAfter(LocalDate date);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Optional<User> findByEmailOrPhone(String login);
}