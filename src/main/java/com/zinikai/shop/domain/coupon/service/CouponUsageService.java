package com.zinikai.shop.domain.coupon.service;

import com.zinikai.shop.domain.coupon.dto.CouponUsageResponseDto;

public interface CouponUsageService {

    CouponUsageResponseDto getCouponUsageInfo(String memberUuid, String couponUsageUuid);
}
