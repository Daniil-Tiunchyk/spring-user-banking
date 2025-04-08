package com.example.spring_user_banking.service;

import com.example.spring_user_banking.dao.UserDao;
import com.example.spring_user_banking.exception.UnauthorizedException;
import com.example.spring_user_banking.security.JwtTokenProvider;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserDao userDao;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserDao userDao, JwtTokenProvider jwtTokenProvider) {
        this.userDao = userDao;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String authenticate(String login, String password) {
        return userDao.findByEmailOrPhone(login)
                .filter(user -> password.equals(user.getPassword()))
                .map(user -> jwtTokenProvider.generateToken(user.getId()))
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
    }
}
