package com.example.spring_user_banking.service;

import com.example.spring_user_banking.dao.UserDao;
import com.example.spring_user_banking.config.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserDao userDao,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String authenticate(String login, String password) {
        return userDao.findByEmailOrPhone(login)
                .filter(user -> passwordEncoder.matches(password, user.getPasswordHash()))
                .map(user -> jwtTokenProvider.generateToken(user.getId()))
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
    }
}
