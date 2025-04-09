package com.example.spring_user_banking.controller;

import com.example.spring_user_banking.dto.EmailDTO;
import com.example.spring_user_banking.dto.PhoneDTO;
import com.example.spring_user_banking.dto.UserDTO;
import com.example.spring_user_banking.mapper.UserMapper;
import com.example.spring_user_banking.model.User;
import com.example.spring_user_banking.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "User API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Получение идентификатора текущего пользователя из SecurityContextHolder.
     */
    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    @ApiOperation(value = "Получить пользователя по ID")
    @GetMapping("/{userId}")
    public UserDTO getUserById(
            @ApiParam(value = "ID пользователя", required = true)
            @PathVariable Long userId
    ) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return UserMapper.toUserDTO(user);
    }

    @ApiOperation(value = "Поиск пользователей с фильтрами и пагинацией")
    @GetMapping
    public List<UserDTO> searchUsers(
            @ApiParam(value = "Префикс имени")
            @RequestParam(required = false) String name,

            @ApiParam(value = "Email (точное совпадение)")
            @RequestParam(required = false) String email,

            @ApiParam(value = "Телефон (точное совпадение)")
            @RequestParam(required = false) String phone,

            @ApiParam(value = "Дата рождения 'больше чем' (dd.MM.yyyy)")
            @RequestParam(required = false) String dateOfBirth,

            @ApiParam(value = "Номер страницы (offset)", defaultValue = "0")
            @RequestParam(defaultValue = "0") int page,

            @ApiParam(value = "Размер страницы (limit)", defaultValue = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        LocalDate dobFilter = null;
        if (dateOfBirth != null && !dateOfBirth.isBlank()) {
            try {
                dobFilter = LocalDate.parse(dateOfBirth, java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            } catch (DateTimeParseException e) {
                throw new RuntimeException("Некорректный формат даты (ожидается dd.MM.yyyy)");
            }
        }

        List<User> users = userService.searchUsers(name, email, phone, dobFilter, page, size);
        return users.stream()
                .map(UserMapper::toUserDTO)
                .collect(Collectors.toList());
    }


    @ApiOperation(value = "Добавить e-mail пользователю (только себе)")
    @PostMapping("/{userId}/emails")
    public void addEmail(
            @ApiParam(value = "ID пользователя (должен совпадать с авторизованным)", required = true)
            @PathVariable Long userId,

            @ApiParam(value = "DTO с email")
            @RequestBody EmailDTO emailDTO
    ) {
        Long currentUserId = getCurrentUserId();
        userService.addEmail(currentUserId, userId, emailDTO.getEmail());
    }

    @ApiOperation(value = "Удалить e-mail у пользователя (только у себя)")
    @DeleteMapping("/{userId}/emails")
    public void removeEmail(
            @ApiParam(value = "ID пользователя (должен совпадать с авторизованным)", required = true)
            @PathVariable Long userId,
            @RequestBody EmailDTO emailDTO
    ) {
        Long currentUserId = getCurrentUserId();
        userService.removeEmail(currentUserId, userId, emailDTO.getEmail());
    }

    @ApiOperation(value = "Добавить телефон пользователю (только себе)")
    @PostMapping("/{userId}/phones")
    public void addPhone(
            @ApiParam(value = "ID пользователя (должен совпадать с авторизованным)", required = true)
            @PathVariable Long userId,
            @RequestBody PhoneDTO phoneDTO
    ) {
        Long currentUserId = getCurrentUserId();
        userService.addPhone(currentUserId, userId, phoneDTO.getPhone());
    }

    @ApiOperation(value = "Удалить телефон у пользователя (только у себя)")
    @DeleteMapping("/{userId}/phones")
    public void removePhone(
            @ApiParam(value = "ID пользователя (должен совпадать с авторизованным)", required = true)
            @PathVariable Long userId,
            @RequestBody PhoneDTO phoneDTO
    ) {
        Long currentUserId = getCurrentUserId();
        userService.removePhone(currentUserId, userId, phoneDTO.getPhone());
    }
}
