package com.example.spring_user_banking.controller;

import com.example.spring_user_banking.dto.TransferRequestDTO;
import com.example.spring_user_banking.dto.UserDTO;
import com.example.spring_user_banking.mapper.UserMapper;
import com.example.spring_user_banking.model.User;
import com.example.spring_user_banking.service.AccountService;
import com.example.spring_user_banking.service.TransferService;
import com.example.spring_user_banking.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Api(tags = "Account API")
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransferService transferService;
    private final UserService userService;

    @ApiOperation("Получить информацию о текущем пользователе (с балансом)")
    @GetMapping
    public UserDTO getMyAccount(
            @ApiParam(value = "Авторизованный userId")
            @RequestParam Long currentUserId
    ) {
        User user = userService.getUserById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return UserMapper.toUserDTO(user);
    }

    @ApiOperation("Перевести деньги другому пользователю")
    @PostMapping("/transfer")
    public void transferMoney(@RequestBody TransferRequestDTO dto) {
        Long fromUserId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        transferService.transferMoney(fromUserId, dto.getToUserId(), dto.getAmount());
    }

    //TODO Удлаить после тестов
    @ApiOperation("Пример прямого обновления баланса (для тестов)")
    @PutMapping
    public void updateMyBalance(
            @ApiParam(value = "Авторизованный userId") @RequestParam Long currentUserId,
            @ApiParam(value = "Новый баланс") @RequestParam BigDecimal newBalance
    ) {
        boolean ok = accountService.updateBalance(currentUserId, newBalance);
        if (!ok) {
            throw new RuntimeException("Не удалось обновить баланс");
        }
    }
}
