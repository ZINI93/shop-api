package com.zinikai.shop.domain.product.exception;

public class ProductNotFoundException extends RuntimeException {


    public ProductNotFoundException(String message) {
        super(message);
    }
}
