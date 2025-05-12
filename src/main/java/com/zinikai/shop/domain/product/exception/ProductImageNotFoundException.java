package com.zinikai.shop.domain.product.exception;

public class ProductImageNotFoundException extends RuntimeException {
    public ProductImageNotFoundException(String message) {
        super(message);
    }
}
