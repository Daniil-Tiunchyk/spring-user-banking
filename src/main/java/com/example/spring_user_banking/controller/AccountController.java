package com.example.spring_user_banking.controller;

import com.example.spring_user_banking.dto.TransferRequestDTO;
import com.example.spring_user_banking.dto.UserDTO;
import com.example.spring_user_banking.mapper.UserMapper;
import com.example.spring_user_banking.model.User;
import com.example.spring_user_banking.service.TransferService;
import com.example.spring_user_banking.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Account API")
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final TransferService transferService;
    private final UserService userService;

    /**
     * Получение идентификатора текущего пользователя из SecurityContextHolder.
     */
    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @ApiOperation("Получить информацию о текущем пользователе (с балансом)")
    @GetMapping
    public UserDTO getMyAccount(
            @ApiParam(value = "Авторизованный userId") @RequestParam Long currentUserId
    ) {
        Long authenticatedUserId = getCurrentUserId();
        // Проверка: запрашиваемый userId должен соответствовать авторизованному пользователю.
        if (!currentUserId.equals(authenticatedUserId)) {
            throw new SecurityException("Нельзя запрашивать данные другого пользователя!");
        }
        User user = userService.getUserById(authenticatedUserId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return UserMapper.toUserDTO(user);
    }

    @ApiOperation("Перевести деньги другому пользователю")
    @PostMapping("/transfer")
    public void transferMoney(@RequestBody TransferRequestDTO dto) {
        Long fromUserId = getCurrentUserId();
        transferService.transferMoney(fromUserId, dto.getToUserId(), dto.getAmount());
    }
}
