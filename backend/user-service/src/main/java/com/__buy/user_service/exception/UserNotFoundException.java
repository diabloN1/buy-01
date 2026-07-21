package com.__buy.user_service.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String id) {
        super("User with ID '" + id + "' not found.");
    }
}