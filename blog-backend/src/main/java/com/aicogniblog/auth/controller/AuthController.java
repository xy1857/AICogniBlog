package com.aicogniblog.auth.controller;

import com.aicogniblog.auth.dto.LoginRequest;
import com.aicogniblog.auth.dto.LoginResponse;
import com.aicogniblog.auth.dto.RegisterRequest;
import com.aicogniblog.auth.service.AuthService;
import com.aicogniblog.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT 无状态，客户端清除 Token 即可
        return Result.success("已登出");
    }
}
