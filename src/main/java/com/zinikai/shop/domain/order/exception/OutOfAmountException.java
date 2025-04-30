package com.zinikai.shop.domain.order.exception;

public class OutOfAmountException extends RuntimeException {
    public OutOfAmountException(String message) {
        super(message);
    }
}
