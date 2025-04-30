package com.zinikai.shop.domain.coupon.exception;

public class CouponNotFoundException extends RuntimeException {
    public CouponNotFoundException(String message) {
        super(message);
    }
}
