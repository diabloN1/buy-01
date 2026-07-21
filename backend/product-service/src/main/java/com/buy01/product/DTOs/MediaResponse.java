package com.buy01.product.DTOs;

public record MediaResponse(
        String id,
        String path,
        String productId,
        String contentType
) {
}