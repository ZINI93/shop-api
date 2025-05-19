package com.zinikai.shop.domain.delivery.exception;

public class DeliveryStateMissMatchException extends RuntimeException {
    public DeliveryStateMissMatchException(String message) {
        super(message);
    }
}
