package com.buy01.product.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;

import com.buy01.product.DTOs.CreateRequest;
import com.buy01.product.DTOs.ProductImageResponse;
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

    @Value("${media.base-url}")
    private String mediaBaseUrl;

    private final ProductMediaService productMediaService;
    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(CreateRequest req, List<MultipartFile> images) {

        String currentUserId = getCurrentUUID();

        Product product = Product.builder()
                .name(req.name())
                .description(req.description())
                .price(req.price())
                .quantity(req.quantity())
                .userId(currentUserId)
                .imageIds(new ArrayList<>())
                .build();

        Product saved = productRepository.save(product);

        try {

            saved.setImageIds(productMediaService.uploadImages(images, saved.getId()));

            saved = productRepository.save(saved);

            return mapToResponse(saved);

        } catch (Exception ex) {
            productRepository.deleteById(saved.getId());
            throw ex;
        }
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
    public ProductResponse updateProduct(
            String id,
            UpdateRequest updated,
            List<MultipartFile> images,
            List<String> deletedImageIds) {

        Product product = findProductEntityById(id);

        if (!isCurrentOwnerOrAdmin(product.getUserId())) {
            throw new ForbiddenException(
                    "Sorry! You are not the owner of this product");
        }

        product.setName(updated.name());
        product.setDescription(updated.description());
        product.setPrice(updated.price());
        product.setQuantity(updated.quantity());

        if (product.getImageIds() == null) {
            product.setImageIds(new ArrayList<>());
        }

        if (deletedImageIds != null && !deletedImageIds.isEmpty()) {

            product.getImageIds()
                    .removeAll(deletedImageIds);

            productMediaService.deleteImages(
                    deletedImageIds);
        }

        if (images != null && !images.isEmpty()) {

            List<String> newImages = productMediaService.uploadImages(
                    images,
                    product.getId());

            product.getImageIds()
                    .addAll(newImages);
        }

        return mapToResponse(
                productRepository.save(product));
    }

    @Override
    public void deleteProduct(String id) {
        Product product = findProductEntityById(id);

        System.out.println("Owner: " + product.getUserId());
        System.out.println("Current: " + getCurrentUUID());
        System.out.println("Role: " + getCurrentRole());

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

        List<ProductImageResponse> images = product.getImageIds()
                .stream()
                .map(imageId -> new ProductImageResponse(
                        imageId,
                        mediaBaseUrl + "/" + imageId))
                .toList();

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                images,
                product.getPrice(),
                product.getUserId(),
                product.getQuantity(),
                product.getCreatedAt());
    }

    private boolean isCurrentOwnerOrAdmin(String ownerId) {
        String currentUserId = getCurrentUUID();
        String currentRole = getCurrentRole();

        return ownerId.equals(currentUserId)
                || "ADMIN".equals(currentRole);
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
                .peek(a -> System.out.println("Authority = " + a.getAuthority()))
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replaceFirst("^ROLE_", ""))
                .orElse(null);
    }

    @Override
    public long countProducts() {
        return productRepository.count();
    }
}