package com.__buy.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @NotBlank @Size(min = 3, max = 25)
    private String name;

    @Email
    @NotBlank
    private String email;
}
