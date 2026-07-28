package com.__buy.user_service.service;

import com.__buy.user_service.dto.AuthResult;
import com.__buy.user_service.dto.LoginRequest;
import com.__buy.user_service.dto.RegisterRequest;

public interface AuthService {
    AuthResult register(RegisterRequest request);

    AuthResult login(LoginRequest request);

    AuthResult refreshToken(String refreshToken);
}