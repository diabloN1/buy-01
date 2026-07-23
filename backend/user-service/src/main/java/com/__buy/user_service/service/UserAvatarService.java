package com.__buy.user_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.__buy.user_service.client.MediaClient;
import com.__buy.user_service.dto.MediaResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAvatarService {

    private final MediaClient mediaClient;

    public String uploadAvatar(MultipartFile avatar) {

        MediaResponse media = mediaClient.upload(avatar, null);
        return media.id();
    }

    public void deleteAvatar(String avatarId) {

        if (avatarId == null) {
            return;
        }

        mediaClient.delete(avatarId);
    }
}