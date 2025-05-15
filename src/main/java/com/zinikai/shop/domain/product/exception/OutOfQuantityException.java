package com.zinikai.shop.domain.product.exception;

public class OutOfQuantityException extends RuntimeException {
    public OutOfQuantityException(String message) {
        super(message);
    }
}
