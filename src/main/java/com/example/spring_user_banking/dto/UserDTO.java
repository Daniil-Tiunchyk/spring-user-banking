package com.example.spring_user_banking.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("User DTO")
public class UserDTO {

    @ApiModelProperty("Идентификатор пользователя")
    private Long id;

    @ApiModelProperty("Имя пользователя")
    private String name;

    @ApiModelProperty("Дата рождения (формат dd.MM.yyyy)")
    private LocalDate dateOfBirth;

    @ApiModelProperty("Список e-mail-адресов пользователя")
    private List<String> emails;

    @ApiModelProperty("Список телефонных номеров пользователя")
    private List<String> phones;

    @ApiModelProperty("Текущий баланс (рубли и копейки)")
    private BigDecimal balance;
}
