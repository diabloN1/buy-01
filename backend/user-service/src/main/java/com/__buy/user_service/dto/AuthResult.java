package com.__buy.user_service.dto;

public record AuthResult(
        String accessToken,
        String refreshToken,
        UserResponse user) {
}