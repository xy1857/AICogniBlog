package com.aicogniblog.auth.service;

import com.aicogniblog.auth.dto.LoginRequest;
import com.aicogniblog.auth.dto.LoginResponse;
import com.aicogniblog.auth.dto.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}
