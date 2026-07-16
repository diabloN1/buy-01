package com.__buy.user_service.service;

import com.__buy.user_service.dto.CreateUserRequest;
import com.__buy.user_service.dto.UserResponse;

public interface UserService {
    UserResponse createUser(CreateUserRequest user);
}
