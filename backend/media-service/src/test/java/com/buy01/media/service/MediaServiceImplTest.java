package com.buy01.media.service;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.apache.tika.Tika;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.buy01.media.client.ProductClient;
import com.buy01.media.client.UserClient;
import com.buy01.media.entity.Media;
import com.buy01.media.repository.MediaRepository;

import software.amazon.awssdk.services.s3.S3Client;

@ExtendWith(MockitoExtension.class)
@DisplayName("MediaServiceImpl Unit Tests")
public class MediaServiceImplTest {
    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private ProductClient productClient;

    @Mock
    private S3Client s3Client;

    @Mock
    private Tika tika;

    @InjectMocks
    private MediaServiceImpl mediaService;

    private final String id = "image-1";
    private final String path = "UNIQUE-ID.png";
    private final String productId = "product-1";
    private final String userId = "user-1";
    private final String contentType = "image/png";

    private Media media;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                mediaService,
                "bucket",
                "images-bucket");

        media = Media.builder()
                .id(id)
                .path(path)
                .userId(userId)
                .productId(productId)
                .contentType(contentType)
                .build();
    }

    @Nested
    @DisplayName("get()")
    class Get {
        @Test
        @DisplayName("Should return media requested")
        void get_validId_ShouldReturnMedia() {
            // given
            when(mediaRepository.findById(id)).thenReturn(Optional.of(media));

            // when
            Media result = mediaService.get(id);

            // then
            assertThat(result).usingRecursiveAssertion().isEqualTo(result);
        }
    }

}
