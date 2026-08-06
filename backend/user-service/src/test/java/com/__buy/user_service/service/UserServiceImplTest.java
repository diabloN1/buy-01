package com.__buy.user_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

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

    @Nested
    @DisplayName("getAllUsers()")
    class GetAllUsers {

        @Test
        @DisplayName("should return a page of UserResponse")
        void getAllUsers_returnsPageOfUsers() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Page<User> userPage = new PageImpl<>(List.of(user));

            when(userRepo.findAll(pageable)).thenReturn(userPage);
            when(userMapper.toResponse(user)).thenReturn(userResponse);

            // when
            Page<UserResponse> result = userService.getAllUsers(pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(USER_ID);
        }
    }

    @Nested
    @DisplayName("createUser()")
    class CreateUser {

        private CreateUserRequest request;

        @BeforeEach
        void setUp() {
            request = new CreateUserRequest();
            request.setName(NAME);
            request.setEmail(EMAIL);
            request.setPassword(RAW_PASS);
        }

        @Test
        @DisplayName("should create and return UserResponse when email is available")
        void createUser_success_returnsUserResponse() {
            // given
            when(userRepo.existsByEmail(EMAIL)).thenReturn(false);
            when(passwordEncoder.encode(RAW_PASS)).thenReturn(HASHED_PASS);
            when(userRepo.save(any(User.class))).thenReturn(user);
            when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

            // when
            UserResponse result = userService.createUser(request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(EMAIL);
            verify(userRepo).save(any(User.class));
        }

        @Test
        @DisplayName("should throw EmailAlreadyExistsException when email is taken")
        void createUser_duplicateEmail_throwsEmailAlreadyExistsException() {
            // given
            when(userRepo.existsByEmail(EMAIL)).thenReturn(true);

            // when / then
            assertThatThrownBy(() -> userService.createUser(request))
                    .isInstanceOf(EmailAlreadyExistsException.class);

            verify(userRepo, never()).save(any());
            verify(passwordEncoder, never()).encode(any());
        }
    }

    @Nested
    @DisplayName("updateUser()")
    class UpdateUser {

        private UpdateUserRequest request;

        @BeforeEach
        void setUp() {
            request = new UpdateUserRequest();
            request.setName("New Name");
            request.setEmail("new@gmail.com");
        }

        @Test
        @DisplayName("should update name and email and return UserResponse")
        void updateUser_success_returnsUpdatedUserResponse() {
            // given
            when(userRepo.findById(USER_ID)).thenReturn(Optional.of(user));
            when(userRepo.findByEmail("new@gmail.com")).thenReturn(Optional.empty());
            when(userRepo.save(any(User.class))).thenReturn(user);
            when(userMapper.toResponse(user)).thenReturn(userResponse);

            // when
            UserResponse result = userService.updateUser(USER_ID, request);

            // then
            assertThat(result).isNotNull();
            verify(userRepo).save(user);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when user does not exist")
        void updateUser_userNotFound_throwsUserNotFoundException() {
            // given
            when(userRepo.findById(USER_ID)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userService.updateUser(USER_ID, request))
                    .isInstanceOf(UserNotFoundException.class);

            verify(userRepo, never()).save(any());
        }

        @Test
        @DisplayName("should throw EmailAlreadyExistsException when new email is taken")
        void updateUser_emailTaken_throwsEmailAlreadyExistsException() {
            // given
            User other = User.builder().id("other-456").email("new@gmail.com").build();

            when(userRepo.findById(USER_ID)).thenReturn(Optional.of(user));
            when(userRepo.findByEmail("new@gmail.com")).thenReturn(Optional.of(other));

            // when / then
            assertThatThrownBy(() -> userService.updateUser(USER_ID, request))
                    .isInstanceOf(EmailAlreadyExistsException.class);

            verify(userRepo, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteUser()")
    class DeleteUser {

        @Test
        @DisplayName("should delete the user and call avatarService.deleteAvatar")
        void deleteUser_success_deletesUserAndAvatar() {
            // given
            user.setAvatarId(AVATAR_ID);
            when(userRepo.findById(USER_ID)).thenReturn(Optional.of(user));

            // when
            userService.deleteUser(USER_ID);

            // then
            verify(avatarService).deleteAvatar(AVATAR_ID);
            verify(userRepo).delete(user);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when user does not exist")
        void deleteUser_userNotFound_throwsUserNotFoundException() {
            // given
            when(userRepo.findById(USER_ID)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userService.deleteUser(USER_ID))
                    .isInstanceOf(UserNotFoundException.class);

            verify(userRepo, never()).delete(any());
            verify(avatarService, never()).deleteAvatar(any());
        }
    }

    @Nested
    @DisplayName("countUsers()")
    class CountUsers {

        @Test
        @DisplayName("should return the total number of users")
        void countUsers_returnsCount() {
            // given
            when(userRepo.count()).thenReturn(5L);

            // when
            long count = userService.countUsers();

            // then
            assertThat(count).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("uploadAvatar()")
    class UploadAvatar {

        @Test
        @DisplayName("should upload avatar, save new avatarId and delete old one")
        void uploadAvatar_withOldAvatar_replacesAndDeletesOld() {
            // given
            user.setAvatarId("old-avatar");
            MultipartFile file = mock(MultipartFile.class);

            when(userRepo.findById(USER_ID)).thenReturn(Optional.of(user));
            when(avatarService.uploadAvatar(file)).thenReturn(AVATAR_ID);
            when(userRepo.save(any(User.class))).thenReturn(user);
            when(userMapper.toResponse(user)).thenReturn(userResponse);

            // when
            UserResponse result = userService.uploadAvatar(USER_ID, file);

            // then
            assertThat(result).isNotNull();
            verify(avatarService).uploadAvatar(file);
            verify(avatarService).deleteAvatar("old-avatar");
            verify(userRepo).save(user);
        }

        @Test
        @DisplayName("should upload avatar without deleting when no previous avatar exists")
        void uploadAvatar_noOldAvatar_uploadsWithoutDeleting() {
            // given
            MultipartFile file = mock(MultipartFile.class);

            when(userRepo.findById(USER_ID)).thenReturn(Optional.of(user));
            when(avatarService.uploadAvatar(file)).thenReturn(AVATAR_ID);
            when(userRepo.save(any(User.class))).thenReturn(user);
            when(userMapper.toResponse(user)).thenReturn(userResponse);

            // when
            userService.uploadAvatar(USER_ID, file);

            // then
            verify(avatarService).uploadAvatar(file);
            verify(avatarService, never()).deleteAvatar(any());
        }

        @Test
        @DisplayName("should throw UserNotFoundException when user does not exist")
        void uploadAvatar_userNotFound_throwsUserNotFoundException() {
            // given
            MultipartFile file = mock(MultipartFile.class);
            when(userRepo.findById(USER_ID)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userService.uploadAvatar(USER_ID, file))
                    .isInstanceOf(UserNotFoundException.class);

            verify(avatarService, never()).uploadAvatar(any());
        }
    }

    @Nested
    @DisplayName("deleteAvatar()")
    class DeleteAvatar {

        @Test
        @DisplayName("should clear avatarId and save user")
        void deleteAvatar_success_clearsAvatarId() {
            // given
            user.setAvatarId(AVATAR_ID);
            when(userRepo.findById(USER_ID)).thenReturn(Optional.of(user));

            // when
            userService.deleteAvatar(USER_ID);

            // then
            assertThat(user.getAvatarId()).isNull();
            verify(userRepo).save(user);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when user does not exist")
        void deleteAvatar_userNotFound_throwsUserNotFoundException() {
            // given
            when(userRepo.findById(USER_ID)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userService.deleteAvatar(USER_ID))
                    .isInstanceOf(UserNotFoundException.class);

            verify(userRepo, never()).save(any());
        }
    }
}
