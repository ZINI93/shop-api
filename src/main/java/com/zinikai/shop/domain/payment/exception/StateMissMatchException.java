package com.zinikai.shop.domain.payment.exception;

public class StateMissMatchException extends RuntimeException {
    public StateMissMatchException(String message) {
        super(message);
    }
}
