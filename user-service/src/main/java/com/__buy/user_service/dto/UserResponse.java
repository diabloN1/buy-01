package com.__buy.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private String id;

    private String firstName;

    private String lastName;

    private String email;
}
