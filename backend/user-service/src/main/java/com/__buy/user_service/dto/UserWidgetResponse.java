package com.__buy.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWidgetResponse {

    private String id;

    private String name;

    private UserAvatarResponse avatarId;

    public UserWidgetResponse(UserResponse user) {
        this.id = user.getId();
        this.name = user.getName();
        this.avatarId = user.getAvatar();
    }
}