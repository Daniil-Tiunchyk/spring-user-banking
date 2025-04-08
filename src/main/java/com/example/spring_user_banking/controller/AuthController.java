package com.example.spring_user_banking.controller;

import com.example.spring_user_banking.dto.AuthRequest;
import com.example.spring_user_banking.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Api(tags = "Auth API")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @ApiOperation(value = "Вход пользователя", notes = "Аутентифицирует пользователя по логину и паролю, возвращает JWT-токен")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешная аутентификация"),
            @ApiResponse(code = 401, message = "Неверные учетные данные")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @ApiParam(value = "Тело запроса для аутентификации, содержащее логин и пароль", required = true)
            @RequestBody AuthRequest request) {
        String token = authService.authenticate(request.getLogin(), request.getPassword());
        return ResponseEntity.ok(token);
    }
}
