package com.example.spring_user_banking.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("Transfer Request")
public class TransferRequestDTO {

    @ApiModelProperty("ID пользователя-получателя")
    private Long toUserId;

    @ApiModelProperty("Сумма перевода")
    private BigDecimal amount;
}
