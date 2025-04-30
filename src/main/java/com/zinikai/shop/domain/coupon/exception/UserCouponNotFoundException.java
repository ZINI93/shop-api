package com.zinikai.shop.domain.coupon.exception;

public class UserCouponNotFoundException extends RuntimeException {
    public UserCouponNotFoundException(String message) {
        super(message);
    }
}
