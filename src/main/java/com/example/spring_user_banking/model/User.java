package com.example.spring_user_banking.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString(exclude = "password")
@EqualsAndHashCode(exclude = "password")
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private String password;

    private List<String> emails;
    private List<String> phones;
    private BigDecimal balance;
}
