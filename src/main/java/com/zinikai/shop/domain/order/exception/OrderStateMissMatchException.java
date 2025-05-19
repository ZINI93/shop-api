package com.zinikai.shop.domain.order.exception;

public class OrderStateMissMatchException extends RuntimeException {
    public OrderStateMissMatchException(String message) {
        super(message);
    }
}
