package com.__buy.user_service.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.__buy.user_service.aop.Auditable;
import com.__buy.user_service.dto.AuthResponse;
import com.__buy.user_service.dto.LoginRequest;
import com.__buy.user_service.dto.RefreshTokenRequest;
import com.__buy.user_service.dto.RegisterRequest;
import com.__buy.user_service.dto.UserResponse;
import com.__buy.user_service.entity.RefreshToken;
import com.__buy.user_service.entity.Role;
import com.__buy.user_service.entity.User;
import com.__buy.user_service.event.AuditAction;
import com.__buy.user_service.exception.ConflictException;
import com.__buy.user_service.exception.UnauthorizedException;
import com.__buy.user_service.repository.UserRepository;
import com.__buy.user_service.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Auditable(action = AuditAction.CREATED, entityId = "#result.user.id")
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ConflictException("Email already exists");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        user.setRole(Role.valueOf(request.role().name()));

        user = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                new UserResponse(user));
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                new UserResponse(user));
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken refreshToken = jwtService.validateRefreshToken(request.refreshToken());

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        String accessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
                accessToken,
                newRefreshToken,
                new UserResponse(user));
    }
}
