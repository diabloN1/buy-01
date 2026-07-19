package com.buy01.product.exception.custom;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}