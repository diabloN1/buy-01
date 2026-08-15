package com.buy01.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.buy01.product.DTOs.MediaResponse;
import com.buy01.product.client.MediaClient;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductMediaService Unit Tests")
class ProductMediaServiceTest {

    @Mock
    private MediaClient mediaClient;

    @InjectMocks
    private ProductMediaService productMediaService;

    private static final String IMAGE_ID_1 = "IMAGE-123";
    private static final String IMAGE_ID_2 = "IMAGE-456";
    private static final String PRODUCT_ID = "PRODUCT-123";

    private MultipartFile image1;
    private MultipartFile image2;

    private MediaResponse mediaResponse1;
    private MediaResponse mediaResponse2;

    @BeforeEach
    void setUp() {
        image1 = mock(MultipartFile.class);
        image2 = mock(MultipartFile.class);

        mediaResponse1 = MediaResponse.builder()
                .id(IMAGE_ID_1)
                .build();

        mediaResponse2 = MediaResponse.builder()
                .id(IMAGE_ID_2)
                .build();
    }

    @Nested
    @DisplayName("uploadImages()")
    class UploadImages {

        @Test
        @DisplayName("Should upload image and return its id")
        void uploadImages_shouldUploadImageAndReturnId() {
            // given
            when(mediaClient.upload(image1, PRODUCT_ID))
                    .thenReturn(mediaResponse1);

            // when
            List<String> imageIds = productMediaService.uploadImages(
                    List.of(image1),
                    PRODUCT_ID
            );

            // then
            assertThat(imageIds)
                    .containsExactly(IMAGE_ID_1);

            verify(mediaClient).upload(image1, PRODUCT_ID);
        }

        @Test
        @DisplayName("Should upload all images and return their ids")
        void uploadImages_shouldUploadAllImagesAndReturnIds() {
            // given
            when(mediaClient.upload(image1, PRODUCT_ID))
                    .thenReturn(mediaResponse1);

            when(mediaClient.upload(image2, PRODUCT_ID))
                    .thenReturn(mediaResponse2);

            // when
            List<String> imageIds = productMediaService.uploadImages(
                    List.of(image1, image2),
                    PRODUCT_ID
            );

            // then
            assertThat(imageIds)
                    .containsExactly(IMAGE_ID_1, IMAGE_ID_2);

            verify(mediaClient).upload(image1, PRODUCT_ID);
            verify(mediaClient).upload(image2, PRODUCT_ID);
        }
    }

    @Nested
    @DisplayName("deleteImages()")
    class DeleteImages {

        @Test
        @DisplayName("Should delete all images")
        void deleteImages_shouldDeleteAllImages() {
            // given
            List<String> imageIds = List.of(IMAGE_ID_1, IMAGE_ID_2);

            // when
            productMediaService.deleteImages(imageIds);

            // then
            verify(mediaClient).delete(IMAGE_ID_1);
            verify(mediaClient).delete(IMAGE_ID_2);
        }

        @Test
        @DisplayName("Should not delete media when image ids are null")
        void deleteImages_shouldNotDeleteWhenIdsNull() {
            // when
            productMediaService.deleteImages(null);

            // then
            verify(mediaClient, never()).delete(anyString());
        }

        @Test
        @DisplayName("Should not delete media when image ids are empty")
        void deleteImages_shouldNotDeleteWhenIdsEmpty() {
            // when
            productMediaService.deleteImages(List.of());

            // then
            verify(mediaClient, never()).delete(anyString());
        }
    }
}