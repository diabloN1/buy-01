package com.__buy.user_service.dto;

import java.time.LocalDateTime;

import com.__buy.user_service.entity.Role;
import com.__buy.user_service.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String id;

    private String name;

    private String email;

    private Role role;

    private UserAvatarResponse avatar;

    private LocalDateTime createdAt;

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
    }
}