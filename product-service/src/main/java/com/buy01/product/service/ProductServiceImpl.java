package com.buy01.product.service;

import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

import com.buy01.product.DTOs.CreateRequest;
import com.buy01.product.DTOs.UpdateRequest;
import com.buy01.product.DTOs.ProductResponse;
import com.buy01.product.entity.Product;
import com.buy01.product.exception.custom.ForbiddenException;
import com.buy01.product.exception.custom.NotFoundException;
import com.buy01.product.repository.ProductRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(CreateRequest req) {
        String currentUserId = getCurrentUUID();
        log.info("User {} is creating a new product", currentUserId);

        Product product = Product.builder()
                .name(req.name())
                .description(req.description())
                .price(req.price())
                .userId(currentUserId)
                .build();

        return mapToResponse(productRepository.save(product));
    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public ProductResponse getProductById(String id) {
        return mapToResponse(findProductEntityById(id));
    }

    @Override
    public Page<ProductResponse> getProductsByUser(String userId, Pageable pageable) {
        return productRepository.findByUserId(userId, pageable).map(this::mapToResponse);
    }

    @Override
    public ProductResponse updateProduct(String id, UpdateRequest updated) {
        Product product = findProductEntityById(id);

        if (!isCurrentOwnerOrAdmin(product.getUserId())) {
            log.warn("User {} attempted to update product {} without permissions", getCurrentUUID(), id);
            throw new ForbiddenException("Sorry! You are not the owner of this product");
        }

        product.setName(updated.name());
        product.setDescription(updated.description());
        product.setPrice(updated.price());

        log.info("Product {} updated successfully", id);
        return mapToResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(String id) {
        Product product = findProductEntityById(id);

        if (!isCurrentOwnerOrAdmin(product.getUserId())) {
            log.warn("User {} attempted to delete product {} without permissions", getCurrentUUID(), id);
            throw new ForbiddenException("Sorry! You are not the owner of this product");
        }

        productRepository.delete(product);
        log.info("Product {} deleted successfully", id);
    }

    private Product findProductEntityById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product with ID " + id + " not found"));
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getUserId(),
                product.getCreatedAt());
    }

    private boolean isCurrentOwnerOrAdmin(String ownerId) {
        String currentUserId = getCurrentUUID();
        String currentRole = getCurrentRole();
        return ownerId.equals(currentUserId) || "ROLE_ADMIN".equals(currentRole);
    }

    private String getCurrentUUID() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return jwt.getSubject();
    }

    private String getCurrentRole() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replaceFirst("^ROLE_", ""))
                .orElse(null);
    }
}