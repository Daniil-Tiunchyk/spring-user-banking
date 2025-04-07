package com.example.spring_user_banking.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Account {
    private Long id;
    private Long userId;
    private BigDecimal balance;
}
