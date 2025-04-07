package com.example.spring_user_banking.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@ToString
@EqualsAndHashCode
public class User {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private String password;
    private List<String> emails = new ArrayList<>();
    private List<String> phones = new ArrayList<>();
    private BigDecimal balance;

    // Конструкторы
    public User() {}

    public User(Long id, String name, LocalDate dateOfBirth, String password) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.password = password;
    }

    // Форматирование даты
    public String getFormattedDateOfBirth() {
        if (dateOfBirth == null) return null;
        return String.format("%02d.%02d.%d",
                dateOfBirth.getDayOfMonth(),
                dateOfBirth.getMonthValue(),
                dateOfBirth.getYear());
    }

}