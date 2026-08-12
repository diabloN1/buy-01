package com.__buy.user_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.__buy.user_service.client.MediaClient;
import com.__buy.user_service.dto.MediaResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserAvatarService Unit Tests")
public class UserAvatarServiceTest {
    @Mock
    private MediaClient mediaClient;

    @InjectMocks
    private UserAvatarService userAvatarService;  
    
    private static final String avatarId = "AVATAR-123";
    private static final String path = "/path/test.png";
    private static final String productId = "PRODUCT-123";
    private static final String userId = "USER-123";
    private static final String contentType = "image/png";

    private MediaResponse mediaResponse;
    private MultipartFile file;

    @Nested
    @DisplayName("uploadAvatar()")
    class uploadAvatar {
        @BeforeEach
        void setUp() {
            file = mock(MultipartFile.class);

            mediaResponse = MediaResponse.builder()
                    .id(avatarId)
                    .path(path)
                    .productId(productId)
                    .userId(userId)
                    .contentType(contentType)
                    .build();
        }

        @Test
        @DisplayName("Should upload Avatar when valid")
        void uploadAvatar_shouldUploadAvatar() {
            // given 
            when(mediaClient.upload(file, null)).thenReturn(mediaResponse);
            
            // when
            userAvatarService.uploadAvatar(file);
            
            // then
            verify(mediaClient).upload(file, null);
        }

        @Test
        @DisplayName("Should return an Id for the media created")
        void uploadAvatar_shouldReturnMediaId() {
            // given
            when(mediaClient.upload(file, null)).thenReturn(mediaResponse);
            
            // when
            String id = userAvatarService.uploadAvatar(file);

            // then
            assertThat(id).isNotBlank();
        }
    }

    @Nested
    @DisplayName("deleteAvatar()")
    class deleteAvatar {
        @Test
        @DisplayName("Should delete media")
        void deleteAvatar_shouldDeleteMedia() {
            // when
            userAvatarService.deleteAvatar(avatarId);

            // then
            verify(mediaClient).delete(avatarId);
        }

        @Test
        @DisplayName("Should not delete media when no media id provided")
        void deleteAvatar_AvatarNull_S() {
            // when
            userAvatarService.deleteAvatar(null);

            // then
            verify(mediaClient, never()).delete(anyString());
        }
    }
}
