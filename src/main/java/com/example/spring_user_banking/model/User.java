package com.example.spring_user_banking.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(exclude = "password")
@EqualsAndHashCode(exclude = "password")
@AllArgsConstructor
public class User {
    public static final int NAME_MAX_LENGTH = 500;
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final String DATE_FORMAT = "dd.MM.yyyy";

    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private String password;

    private List<String> emails = new ArrayList<>();
    private List<String> phones = new ArrayList<>();
    private BigDecimal balance;
}
