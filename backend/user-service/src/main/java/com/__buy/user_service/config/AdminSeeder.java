package com.__buy.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.__buy.user_service.entity.Role;
import com.__buy.user_service.entity.User;
import com.__buy.user_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.enabled:true}")
    private boolean enabled;

    @Value("${app.admin.name}")
    private String name;

    @Value("${app.admin.email}")
    private String email;

    @Value("${app.admin.password}")
    private String password;

    @Override
    public void run(String... args) {
        if (!enabled || userRepository.existsByEmail(email)) {
            return;
        }

        User admin = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);
    }
}
