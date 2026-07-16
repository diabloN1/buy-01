package com.buy01.product;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.buy01.product.DTOs.CreateRequest;
import com.buy01.product.DTOs.UpdateRequest;
import com.buy01.product.exception.custom.ForbiddenException;
import com.buy01.product.exception.custom.NotFoundException;

import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(CreateRequest req) {
        Product product = new Product();
        product.setName(req.name());
        product.setDescription(req.description());
        product.setPrice(req.price());
        product.setUserId(getCurrentUUID());
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    public List<Product> getProductsByUser(String userId) {
        return productRepository.findByUserId(userId);
    }

    public Product updateProduct(String id, UpdateRequest updated) {
        Product product = getProductById(id);

        if (!isCurrentOwnerOrAdmin(product.getUserId())) {
            throw new ForbiddenException("Sorry! You are not the owner of this product");
        }

        product.setName(updated.name());
        product.setDescription(updated.description());
        product.setPrice(updated.price());

        return productRepository.save(product);
    }

    public void deleteProduct(String id) {
        Product product = getProductById(id);

        if (!isCurrentOwnerOrAdmin(product.getUserId())) {
            throw new ForbiddenException("Sorry! You are not the owner of this product");
        }

        productRepository.delete(product);
    }

    private boolean isCurrentOwnerOrAdmin(String userId) {
        return userId.equals(getCurrentUUID()) || getCurrentRole().equals("ROLE_ADMIN");
    }

    private String getCurrentUUID() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private String getCurrentRole() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);
    }
}