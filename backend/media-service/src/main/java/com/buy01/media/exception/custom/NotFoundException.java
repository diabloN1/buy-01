package com.buy01.media.exception.custom;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}