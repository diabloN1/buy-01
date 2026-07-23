package com.buy01.product.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.buy01.product.DTOs.CreateRequest;
import com.buy01.product.DTOs.ProductResponse;
import com.buy01.product.DTOs.UpdateRequest;
import com.buy01.product.service.ProductService;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PermitAll
    @GetMapping
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productService.getAllProducts(pageable);
    }

    @PermitAll
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponse create(
            @RequestPart("product") @Valid CreateRequest request,
            @RequestPart("images") List<MultipartFile> images) {
        return productService.createProduct(request, images);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponse updateProduct(
            @PathVariable String id,
            @RequestPart("product") @Valid UpdateRequest req,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "deletedImageIds", required = false) List<String> deletedImageIds) {
        return productService.updateProduct(
                id,
                req,
                images,
                deletedImageIds);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELER')")
    public long countProducts() {
        return productService.countProducts();
    }
}