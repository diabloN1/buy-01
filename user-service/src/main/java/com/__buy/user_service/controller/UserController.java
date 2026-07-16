package com.__buy.user_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.__buy.user_service.dto.CreateUserRequest;
import com.__buy.user_service.dto.UserResponse;
import com.__buy.user_service.service.UserServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping()
    public ResponseEntity<UserResponse> postMethodName(@Valid @RequestBody CreateUserRequest userReq) {
        UserResponse userResponse = userService.createUser(userReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

}
