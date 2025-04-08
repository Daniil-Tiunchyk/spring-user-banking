package com.example.spring_user_banking.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("Email DTO")
public class EmailDTO {

    @ApiModelProperty("Email-адрес")
    private String email;
}
