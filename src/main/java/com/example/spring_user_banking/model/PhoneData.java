package com.example.spring_user_banking.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhoneData {
    private Long id;
    private Long userId;
    private String phone;
}
