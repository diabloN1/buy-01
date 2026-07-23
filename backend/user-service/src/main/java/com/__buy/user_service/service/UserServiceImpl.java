package com.__buy.user_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.__buy.user_service.dto.CreateUserRequest;
import com.__buy.user_service.dto.UpdateUserRequest;
import com.__buy.user_service.dto.UserResponse;
import com.__buy.user_service.entity.Role;
import com.__buy.user_service.entity.User;
import com.__buy.user_service.exception.EmailAlreadyExistsException;
import com.__buy.user_service.exception.UserNotFoundException;
import com.__buy.user_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(CreateUserRequest userReq) {
        log.info("Attempting to create user with email: {}", userReq.getEmail());
        if (userRepo.existsByEmail(userReq.getEmail())) {
            throw new EmailAlreadyExistsException(userReq.getEmail());
        }

        User user = User.builder()
                .name(userReq.getName())
                .email(userReq.getEmail())
                .password(passwordEncoder.encode(userReq.getPassword()))
                .role(Role.USER)
                .build();

        userRepo.save(user);
        log.info("Successfully created user ID: {}", user.getId());

        return mapToResponse(user);
    }

    @Override
    public UserResponse getUserById(String id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return mapToResponse(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public UserResponse updateUser(String id, UpdateUserRequest updateReq) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setName(updateReq.getName());

        userRepo.save(user);
        return mapToResponse(user);
    }

    @Override
    public UserResponse getCurrentUser(String userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return mapToResponse(user);
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepo.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepo.deleteById(id);
        log.info("Deleted user ID: {}", id);
    }

    // Helper method to centralize mapping logic
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    public long countUsers() {
        return userRepo.count();
    }
}