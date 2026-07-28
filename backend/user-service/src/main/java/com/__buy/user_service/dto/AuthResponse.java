package com.__buy.user_service.dto;

public record AuthResponse(
                String accessToken,
                UserResponse user) {
}