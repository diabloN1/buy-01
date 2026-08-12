package com.buy01.product.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import com.buy01.product.DTOs.ProductResponse;
import com.buy01.product.client.MediaClient;
import com.buy01.product.entity.Product;
import com.buy01.product.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
    @Mock
    private MediaClient mediaClient;

    @InjectMocks
    private ProductMediaService productMediaService;

    @Mock
    private ProductRepository productRepo;

    @InjectMocks
    private ProductServiceImpl productService;

    private final static String productId = "Product-123";
    private final static String userId = "user-123";
    private final static String name = "product 1";
    private final static String description = "product description";
    private final static BigDecimal price = BigDecimal.valueOf(100);
    private final static Integer quantity = 10;

    private Product product;
    // private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(productId)
                .name(name)
                .description(description)
                .price(price)
                .quantity(quantity)
                .userId(userId)
                .imageIds(List.of())
                .build();

        // productResponse = ProductResponse.builder()
        //         .id(productId)
        //         .name(name)
        //         .description(description)
        //         .price(price)
        //         .quantity(quantity)
        //         .userId(userId)
        //         .images(List.of())
        //         .build();
    }

    @Nested
    @DisplayName("getAllProducts()")
    class GetAllProducts {

        @Test
        @DisplayName("should return a page of ProductResponse")
        void getAllProducts_returnPageOfProduct() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> productPage = new PageImpl<>(List.of(product));

            when(productRepo.findAll(pageable)).thenReturn(productPage);

            // when
            Page<ProductResponse> result = productService.getAllProducts(pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(productId);
        }
    }

    // @Nested
    // @DisplayName(" ")
}
