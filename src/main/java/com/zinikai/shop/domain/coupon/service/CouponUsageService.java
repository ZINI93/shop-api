package com.zinikai.shop.domain.coupon.service;

import com.zinikai.shop.domain.coupon.dto.CouponUsageResponseDto;
import com.zinikai.shop.domain.coupon.entity.UserCoupon;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.order.entity.Orders;

import java.math.BigDecimal;

public interface CouponUsageService {

    CouponUsageResponseDto createCouponUsage(UserCoupon userCoupon, Orders orders, BigDecimal discountAmount);

    CouponUsageResponseDto getCouponUsageInfo(String memberUuid, String couponUsageUuid);
}
