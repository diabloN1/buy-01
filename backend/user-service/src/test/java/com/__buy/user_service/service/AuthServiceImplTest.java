package com.__buy.user_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.__buy.user_service.dto.AuthResult;
import com.__buy.user_service.dto.LoginRequest;
import com.__buy.user_service.dto.RegisterRequest;
import com.__buy.user_service.dto.RegisterRole;
import com.__buy.user_service.entity.Role;
import com.__buy.user_service.entity.User;
import com.__buy.user_service.exception.ConflictException;
import com.__buy.user_service.exception.UnauthorizedException;
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
    private static final String RAW_PASS    = "RAW_PASS_123";
    private static final String HASHED_PASS = "123_SSAP_DEHSAH";
    private static final String ACCESS_TOKEN  = "header.access_payload.signiture";
    private static final String REFRESH_TOKEN = "header.refresh_payload.signiture";

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

        @Test
        @DisplayName("should save the user with an encoded password (never stores plain text)")
        void register_success_passwordIsEncoded() {
            // given
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
            when(passwordEncoder.encode(RAW_PASS)).thenReturn(HASHED_PASS);
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(jwtService.generateAccessToken(any())).thenReturn(ACCESS_TOKEN);
            when(jwtService.generateRefreshToken(any())).thenReturn(REFRESH_TOKEN);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

            // when
            authService.register(request);

            // then
            verify(userRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getPassword())
                    .isEqualTo(HASHED_PASS)
                    .isNotEqualTo(RAW_PASS);
        }

        @Test
        @DisplayName("should assign the correct role from RegisterRequest")
        void register_success_roleIsSetCorrectly() {
            // given
            RegisterRequest sellerRequest = new RegisterRequest(NAME, EMAIL, RAW_PASS, RegisterRole.SELLER);
            User sellerUser = new User();
            sellerUser.setId("seller-456");
            sellerUser.setName(NAME);
            sellerUser.setEmail(EMAIL);
            sellerUser.setPassword(HASHED_PASS);
            sellerUser.setRole(Role.SELLER);

            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenReturn(HASHED_PASS);
            when(userRepository.save(any(User.class))).thenReturn(sellerUser);
            when(jwtService.generateAccessToken(any())).thenReturn(ACCESS_TOKEN);
            when(jwtService.generateRefreshToken(any())).thenReturn(REFRESH_TOKEN);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

            // when
            authService.register(sellerRequest);

            // then
            verify(userRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getRole()).isEqualTo(Role.SELLER);
        }

        @Test
        @DisplayName("should throw ConflictException when email is already registered")
        void register_emailAlreadyExists_throwsConflictException() {
            // given
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(savedUser));

            // when / then
            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage("Email already exists");

            verify(userRepository, never()).save(any());
            verify(passwordEncoder, never()).encode(anyString());
            verify(jwtService, never()).generateAccessToken(any());
            verify(jwtService, never()).generateRefreshToken(any());
        }

        @Test
        @DisplayName("should call generateAccessToken and generateRefreshToken exactly once on success")
        void register_success_tokenGenerationCalledOnce() {
            // given
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
            when(passwordEncoder.encode(RAW_PASS)).thenReturn(HASHED_PASS);
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(jwtService.generateAccessToken(savedUser)).thenReturn(ACCESS_TOKEN);
            when(jwtService.generateRefreshToken(savedUser)).thenReturn(REFRESH_TOKEN);

            // when
            authService.register(request);

            // then
            verify(jwtService).generateAccessToken(savedUser);
            verify(jwtService).generateRefreshToken(savedUser);
        }
    }


    @Nested
    @DisplayName("login()")
    class Login {

        private LoginRequest request;

        @BeforeEach
        void setUp() {
            request = new LoginRequest(EMAIL, RAW_PASS);
        }

        @Test
        @DisplayName("should return AuthResult with tokens when credentials are valid")
        void login_success_returnsAuthResult() {
            // given
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(savedUser));
            when(passwordEncoder.matches(RAW_PASS, HASHED_PASS)).thenReturn(true);
            when(jwtService.generateAccessToken(savedUser)).thenReturn(ACCESS_TOKEN);
            when(jwtService.generateRefreshToken(savedUser)).thenReturn(REFRESH_TOKEN);

            // when
            AuthResult result = authService.login(request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN);
            assertThat(result.refreshToken()).isEqualTo(REFRESH_TOKEN);
            assertThat(result.user()).isNotNull();
            assertThat(result.user().getEmail()).isEqualTo(EMAIL);
        }

        @Test
        @DisplayName("should throw UnauthorizedException when email is not found")
        void login_emailNotFound_throwsUnauthorizedException() {
            // given
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("Invalid credentials");

            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtService, never()).generateAccessToken(any());
            verify(jwtService, never()).generateRefreshToken(any());
        }

        @Test
        @DisplayName("should throw UnauthorizedException when password does not match")
        void login_wrongPassword_throwsUnauthorizedException() {
            // given
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(savedUser));
            when(passwordEncoder.matches(RAW_PASS, HASHED_PASS)).thenReturn(false);

            // when / then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("Invalid credentials");

            verify(jwtService, never()).generateAccessToken(any());
            verify(jwtService, never()).generateRefreshToken(any());
        }

        @Test
        @DisplayName("should call generateAccessToken and generateRefreshToken exactly once")
        void login_success_tokenGenerationCalledOnce() {
            // given
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(savedUser));
            when(passwordEncoder.matches(RAW_PASS, HASHED_PASS)).thenReturn(true);
            when(jwtService.generateAccessToken(savedUser)).thenReturn(ACCESS_TOKEN);
            when(jwtService.generateRefreshToken(savedUser)).thenReturn(REFRESH_TOKEN);

            // when
            authService.login(request);

            // then
            verify(jwtService).generateAccessToken(savedUser);
            verify(jwtService).generateRefreshToken(savedUser);
        }

        @Test
        @DisplayName("should use passwordEncoder.matches to verify password")
        void login_success_usesPasswordEncoderForComparison() {
            // given
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(savedUser));
            when(passwordEncoder.matches(RAW_PASS, HASHED_PASS)).thenReturn(true);
            when(jwtService.generateAccessToken(any())).thenReturn(ACCESS_TOKEN);
            when(jwtService.generateRefreshToken(any())).thenReturn(REFRESH_TOKEN);

            // when
            authService.login(request);

            // then
            verify(passwordEncoder).matches(RAW_PASS, HASHED_PASS);
        }
    }
}
