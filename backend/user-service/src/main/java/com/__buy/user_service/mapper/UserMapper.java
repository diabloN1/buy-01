package com.__buy.user_service.mapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.__buy.user_service.dto.UserAvatarResponse;
import com.__buy.user_service.dto.UserResponse;
import com.__buy.user_service.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {

    @Value("${media.base-url}")
    private String mediaBaseUrl;

    public UserResponse toResponse(User user) {

        UserAvatarResponse avatar = null;

        if (user.getAvatarId() != null) {
            avatar = new UserAvatarResponse(
                    user.getAvatarId(),
                    mediaBaseUrl + "/" + user.getAvatarId());
        }

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .avatar(avatar)
                .createdAt(user.getCreatedAt())
                .build();
    }
}