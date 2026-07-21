package com.buy01.media.exception.custom;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}