package com.example.spring_user_banking.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class AuthRequest {
    private String login;
    private String password;
}
