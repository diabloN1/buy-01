package com.buy01.product.DTOs;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private String id;

    private String name;

    private String description;

    private List<String> imageUrls;

    private BigDecimal price;

    private String userId;

    private Integer quantity;

    private LocalDateTime createdAt;
}