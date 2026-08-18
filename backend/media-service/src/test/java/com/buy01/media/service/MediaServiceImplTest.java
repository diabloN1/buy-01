package com.buy01.media.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;

import com.buy01.media.client.ProductClient;
import com.buy01.media.client.UserClient;
import com.buy01.media.entity.Media;
import com.buy01.media.exception.custom.NotFoundException;
import com.buy01.media.repository.MediaRepository;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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
    private MultipartFile file;

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

        @Test
        @DisplayName("Should throw NotFoundException when invalid Id provided")
        void get_invalidId_shouldThrowNotFoundException() {
            // given
            when(mediaRepository.findById(id)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> mediaService.get(id)).isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("upload()")
    class Upload {

        @BeforeEach
        void setUp() {
            file = mock(MultipartFile.class);
        }

        @Test
        @DisplayName("Should upload file to Object Storage and return saved media")
        void upload_validArgs_ShouldReturnMedia() throws IOException {
            // given
            when(mediaRepository.save(any(Media.class))).thenReturn(media);

            // when
            Media result = mediaService.upload(file, productId);

            // then
            assertThat(result).isEqualTo(media);

            verify(s3Client).putObject(
                    any(PutObjectRequest.class),
                    any(RequestBody.class));

            verify(mediaRepository).save(any(Media.class));
        }

        @Test
        @DisplayName("Should save media with correct product ID and content type")
        void upload_validArgs_ShouldSaveCorrectMediaData() throws IOException {
            // given

            when(mediaRepository.save(any(Media.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            Media result = mediaService.upload(file, productId);

            // then
            assertThat(result.getProductId()).isEqualTo(productId);
            assertThat(result.getContentType()).isEqualTo(contentType);
            assertThat(result.getPath()).endsWith(".png");
        }

        @Test
        @DisplayName("Should use generated object name when uploading to S3")
        void upload_validArgs_ShouldUseGeneratedObjectName() throws IOException {
            // given

            when(mediaRepository.save(any(Media.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            Media result = mediaService.upload(file, productId);

            // then
            verify(s3Client).putObject(
                    argThat((PutObjectRequest request) -> request.bucket().equals("images-bucket")
                            && request.key().equals(result.getPath())
                            && request.contentType().equals(contentType)),
                    any(RequestBody.class));
        }

        @Test
        @DisplayName("Should not save media when file validation fails")
        void upload_invalidFile_ShouldNotSaveMedia() throws IOException {
            // given

            when(file.getOriginalFilename()).thenReturn("file.txt");
            when(file.getContentType()).thenReturn("text/plain");

            // when / then
            assertThatThrownBy(() -> mediaService.upload(file, productId))
                    .isInstanceOf(Exception.class);

            verify(mediaRepository, never()).save(any());
            verify(s3Client, never()).putObject(
                    any(PutObjectRequest.class),
                    any(RequestBody.class));
        }
    }
}
