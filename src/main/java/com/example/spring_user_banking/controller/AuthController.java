package com.example.spring_user_banking.controller;

import com.example.spring_user_banking.dto.AuthRequest;
import com.example.spring_user_banking.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        return authService.authenticate(request.getLogin(), request.getPassword());
    }
}
