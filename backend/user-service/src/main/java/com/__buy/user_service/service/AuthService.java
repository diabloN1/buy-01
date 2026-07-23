package com.__buy.user_service.service;

import com.__buy.user_service.dto.AuthResponse;
import com.__buy.user_service.dto.LoginRequest;
import com.__buy.user_service.dto.RefreshTokenRequest;
import com.__buy.user_service.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);
}