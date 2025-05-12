package com.zinikai.shop.domain.product.exception;

public class OutOfProductImagesException extends RuntimeException {
    public OutOfProductImagesException(String message) {
        super(message);
    }
}
