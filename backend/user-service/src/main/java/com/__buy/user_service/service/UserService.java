package com.__buy.user_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.__buy.user_service.dto.CreateUserRequest;
import com.__buy.user_service.dto.UpdateUserRequest;
import com.__buy.user_service.dto.UserResponse;

public interface UserService {
    UserResponse createUser(CreateUserRequest userReq);

    UserResponse getUserById(String id);

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse updateUser(String id, UpdateUserRequest updateReq);

    UserResponse getCurrentUser(String email);

    void deleteUser(String id);

    long countUsers();

    UserResponse uploadAvatar(String userId, MultipartFile avatar);

    void deleteAvatar(String userId);
}