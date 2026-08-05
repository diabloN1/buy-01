package com.__buy.user_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.__buy.user_service.dto.AuthResult;
import com.__buy.user_service.dto.RegisterRequest;
import com.__buy.user_service.dto.RegisterRole;
import com.__buy.user_service.entity.Role;
import com.__buy.user_service.entity.User;
import com.__buy.user_service.repository.UserRepository;
import com.__buy.user_service.security.JwtService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl Unit Tests")
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private static final String EMAIL       = "amine@gmail.com";
    private static final String NAME        = "Amine Yc";
    private static final String RAW_PASS    = "secret123";
    private static final String HASHED_PASS = "$2a$10$hashedPassword";
    private static final String ACCESS_TOKEN  = "access.jwt.token";
    private static final String REFRESH_TOKEN = "refresh.jwt.token";

    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = User.builder()
                .id("user-123")
                .name(NAME)
                .email(EMAIL)
                .password(HASHED_PASS)
                .role(Role.USER)
                .build();
    }

    @Nested
    @DisplayName("register()")
    class Register {

        private RegisterRequest request;

        @BeforeEach
        void setUp() {
            request = new RegisterRequest(NAME, EMAIL, RAW_PASS, RegisterRole.USER);
        }

        @Test
        @DisplayName("should register a new user and return AuthResult with tokens")
        void register_success_returnsAuthResult() {
            // given
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
            when(passwordEncoder.encode(RAW_PASS)).thenReturn(HASHED_PASS);
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(jwtService.generateAccessToken(savedUser)).thenReturn(ACCESS_TOKEN);
            when(jwtService.generateRefreshToken(savedUser)).thenReturn(REFRESH_TOKEN);

            // when
            AuthResult result = authService.register(request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN);
            assertThat(result.refreshToken()).isEqualTo(REFRESH_TOKEN);
            assertThat(result.user()).isNotNull();
            assertThat(result.user().getEmail()).isEqualTo(EMAIL);
            assertThat(result.user().getName()).isEqualTo(NAME);
        }
    }
}
