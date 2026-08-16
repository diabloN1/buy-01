package com.buy01.product.DTOs;

import lombok.Builder;

@Builder
public record MediaResponse(
        String id,
        String path,
        String productId,
        String contentType
) {
}