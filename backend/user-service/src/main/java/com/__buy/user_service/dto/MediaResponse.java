package com.__buy.user_service.dto;

public record MediaResponse(
        String id,
        String path,
        String productId,
        String userId,
        String contentType) {
}