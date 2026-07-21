package com.buy01.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.buy01.product.DTOs.CreateRequest;
import com.buy01.product.DTOs.ProductResponse;
import com.buy01.product.DTOs.UpdateRequest;

public interface ProductService {
    ProductResponse createProduct(CreateRequest req);

    Page<ProductResponse> getAllProducts(Pageable pageable);

    ProductResponse getProductById(String id);

    Page<ProductResponse> getProductsByUser(String userId, Pageable pageable);

    ProductResponse updateProduct(String id, UpdateRequest updated);

    void deleteProduct(String id);
}