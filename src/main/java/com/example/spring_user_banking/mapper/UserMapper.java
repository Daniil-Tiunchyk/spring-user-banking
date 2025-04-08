package com.example.spring_user_banking.mapper;

import com.example.spring_user_banking.dto.UserDTO;
import com.example.spring_user_banking.model.User;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;

@UtilityClass
public class UserMapper {

    public static UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .dateOfBirth(user.getDateOfBirth())
                .emails(new ArrayList<>(user.getEmails()))
                .phones(new ArrayList<>(user.getPhones()))
                .balance(user.getBalance())
                .build();
    }

}
