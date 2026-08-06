package com.__buy.user_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import com.__buy.user_service.dto.CreateUserRequest;
import com.__buy.user_service.dto.UpdateUserRequest;
import com.__buy.user_service.dto.UserResponse;
import com.__buy.user_service.entity.Role;
import com.__buy.user_service.entity.User;
import com.__buy.user_service.exception.EmailAlreadyExistsException;
import com.__buy.user_service.exception.UserNotFoundException;
import com.__buy.user_service.mapper.UserMapper;
import com.__buy.user_service.repository.UserRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserAvatarService avatarService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private static final String USER_ID    = "user-123";
    private static final String EMAIL      = "amine@gmail.com";
    private static final String NAME       = "Amine Yc";
    private static final String RAW_PASS   = "secret123";
    private static final String HASHED_PASS = "$2a$10$hashedPassword";
    private static final String AVATAR_ID  = "avatar-abc";

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(USER_ID)
                .name(NAME)
                .email(EMAIL)
                .password(HASHED_PASS)
                .role(Role.USER)
                .build();

        userResponse = UserResponse.builder()
                .id(USER_ID)
                .name(NAME)
                .email(EMAIL)
                .role(Role.USER)
                .build();
    }

    @Nested
    @DisplayName("getUserById()")
    class GetUserById {

        @Test
        @DisplayName("should return UserResponse when user exists")
        void getUserById_userExists_returnsUserResponse() {
            // given
            when(userRepo.findById(USER_ID)).thenReturn(Optional.of(user));
            when(userMapper.toResponse(user)).thenReturn(userResponse);

            // when
            UserResponse result = userService.getUserById(USER_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(USER_ID);
            assertThat(result.getEmail()).isEqualTo(EMAIL);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when user does not exist")
        void getUserById_userNotFound_throwsUserNotFoundException() {
            // given
            when(userRepo.findById(USER_ID)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userService.getUserById(USER_ID))
                    .isInstanceOf(UserNotFoundException.class);

            verify(userMapper, never()).toResponse(any());
        }
    }

    @Nested
    @DisplayName("getCurrentUser()")
    class GetCurrentUser {

        @Test
        @DisplayName("should return UserResponse for the authenticated user")
        void getCurrentUser_userExists_returnsUserResponse() {
            // given
            when(userRepo.findById(USER_ID)).thenReturn(Optional.of(user));
            when(userMapper.toResponse(user)).thenReturn(userResponse);

            // when
            UserResponse result = userService.getCurrentUser(USER_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(USER_ID);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when user does not exist")
        void getCurrentUser_userNotFound_throwsUserNotFoundException() {
            // given
            when(userRepo.findById(USER_ID)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userService.getCurrentUser(USER_ID))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

}
