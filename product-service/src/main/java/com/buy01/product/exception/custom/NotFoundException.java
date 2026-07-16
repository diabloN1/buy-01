package com.buy01.product.exception.custom;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}