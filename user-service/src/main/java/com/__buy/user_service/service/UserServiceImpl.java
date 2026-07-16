package com.__buy.user_service.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.__buy.user_service.dto.CreateUserRequest;
import com.__buy.user_service.dto.UserResponse;
import com.__buy.user_service.entity.User;
import com.__buy.user_service.exception.EmailAlreadyExistsException;
import com.__buy.user_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    final private UserRepository userRepo;
    final private PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(CreateUserRequest userReq) {
        if (userRepo.findByEmail(userReq.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(userReq.getEmail());
        }
        User user = User.builder().firstName(userReq.getFirstName()).lastName(userReq.getLastName())
                .email(userReq.getEmail()).password(passwordEncoder.encode(userReq.getPassword()))
                .createdAt(LocalDateTime.now()).build();

        userRepo.save(user);

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail());
    }

}
