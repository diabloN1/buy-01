package com.buy01.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.buy01.product.DTOs.CreateRequest;
import com.buy01.product.DTOs.ProductResponse;
import com.buy01.product.entity.Product;
import com.buy01.product.exception.custom.BadRequestException;
import com.buy01.product.exception.custom.NotFoundException;
import com.buy01.product.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl Unit Tests")
class ProductServiceImplTest {

    @Mock
    private ProductMediaService productMediaService;

    @Mock
    private ProductRepository productRepo;

    @InjectMocks
    private ProductServiceImpl productService;

    private static final String PRODUCT_ID = "Product-123";
    private static final String USER_ID = "user-123";

    private static final String NAME = "product 1";
    private static final String DESCRIPTION = "product description";
    private static final BigDecimal PRICE = BigDecimal.valueOf(100);
    private static final Integer QUANTITY = 10;


    private Product product;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(
                productService,
                "mediaBaseUrl",
                "http://localhost:8082/media"
        );

        product = Product.builder()
                .id(PRODUCT_ID)
                .name(NAME)
                .description(DESCRIPTION)
                .price(PRICE)
                .quantity(QUANTITY)
                .userId(USER_ID)
                .imageIds(new ArrayList<>())
                .build();
    }

    @Nested
    @DisplayName("getAllProducts()")
    class GetAllProducts {

        @Test
        @DisplayName("should return a page of ProductResponse")
        void getAllProducts_returnsPageOfProducts() {

            // given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> productPage =
                    new PageImpl<>(List.of(product));

            when(productRepo.findAll(pageable))
                    .thenReturn(productPage);

            // when
            Page<ProductResponse> result =
                    productService.getAllProducts(pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getId())
                    .isEqualTo(PRODUCT_ID);
        }

        @Test
        @DisplayName("should return empty page when no products exist")
        void getAllProducts_noProducts_returnsEmptyPage() {

            // given
            Pageable pageable = PageRequest.of(0, 10);

            when(productRepo.findAll(pageable))
                    .thenReturn(Page.empty(pageable));

            // when
            Page<ProductResponse> result =
                    productService.getAllProducts(pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("getProductById()")
    class GetProductById {

        @Test
        @DisplayName("should return ProductResponse when product exists")
        void getProductById_productExists_returnsProductResponse() {

            // given
            when(productRepo.findById(PRODUCT_ID))
                    .thenReturn(Optional.of(product));

            // when
            ProductResponse result =
                    productService.getProductById(PRODUCT_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(PRODUCT_ID);
            assertThat(result.getName()).isEqualTo(NAME);
            assertThat(result.getPrice()).isEqualTo(PRICE);
            assertThat(result.getQuantity()).isEqualTo(QUANTITY);
        }

        @Test
        @DisplayName("should throw NotFoundException when product does not exist")
        void getProductById_productNotFound_throwsNotFoundException() {

            // given
            when(productRepo.findById(PRODUCT_ID))
                    .thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(
                    () -> productService.getProductById(PRODUCT_ID))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getProductsByUser()")
    class GetProductsByUser {

        @Test
        @DisplayName("should return products belonging to user")
        void getProductsByUser_returnsPageOfProducts() {

            // given
            Pageable pageable = PageRequest.of(0, 10);

            Page<Product> productPage =
                    new PageImpl<>(List.of(product));

            when(productRepo.findByUserId(USER_ID, pageable))
                    .thenReturn(productPage);

            // when
            Page<ProductResponse> result =
                    productService.getProductsByUser(USER_ID, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getId())
                    .isEqualTo(PRODUCT_ID);
        }

        @Test
        @DisplayName("should return empty page when user has no products")
        void getProductsByUser_noProducts_returnsEmptyPage() {

            // given
            Pageable pageable = PageRequest.of(0, 10);

            when(productRepo.findByUserId(USER_ID, pageable))
                    .thenReturn(Page.empty(pageable));

            // when
            Page<ProductResponse> result =
                    productService.getProductsByUser(USER_ID, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("createProduct()")
    class CreateProduct {

        private CreateRequest request;

        @BeforeEach
        void setUp() {

            request = new CreateRequest(
                    NAME,
                    DESCRIPTION,
                    PRICE,
                    QUANTITY
            );

            setAuthenticatedUser(USER_ID);
        }

        @Test
        @DisplayName("should create product without images")
        void createProduct_noImages_returnsProductResponse() {

            // given
            when(productRepo.save(any(Product.class)))
                    .thenReturn(product);
            
            when(productMediaService.uploadImages(
                    any(),
                    eq(PRODUCT_ID),
                    eq(USER_ID)))
                    .thenReturn(List.of());

            // when
            ProductResponse result =
                    productService.createProduct(request, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(PRODUCT_ID);
            assertThat(result.getName()).isEqualTo(NAME);

            verify(productRepo).save(any(Product.class));
        }

        @Test
        @DisplayName("should throw BadRequestException when more than 5 images are provided")
        void createProduct_tooManyImages_throwsBadRequestException() {

            // given
            List<MultipartFile> images =
                    List.of(
                            mockMultipartFile(),
                            mockMultipartFile(),
                            mockMultipartFile(),
                            mockMultipartFile(),
                            mockMultipartFile(),
                            mockMultipartFile()
                    );

            // when / then
            assertThatThrownBy(
                    () -> productService.createProduct(request, images))
                    .isInstanceOf(BadRequestException.class);

            verify(productRepo, never()).save(any());
            verify(productMediaService, never())
                    .uploadImages(any(), any(), any());
        }
    }


    private void setAuthenticatedUser(String userId) {

        Jwt jwt = Jwt.withTokenValue("test-token")
                .subject(userId)
                .header("alg", "none")
                .build();

        TestingAuthenticationToken authentication =
                new TestingAuthenticationToken(
                        jwt,
                        null,
                        "ROLE_SELLER");

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    private MultipartFile mockMultipartFile() {
        return mock(MultipartFile.class);
    }
}