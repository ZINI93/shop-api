package com.zinikai.shop.domain.coupon.service;

import com.zinikai.shop.domain.coupon.dto.CouponRequestDto;
import com.zinikai.shop.domain.coupon.dto.CouponResponseDto;
import com.zinikai.shop.domain.coupon.dto.CouponUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface CouponService {
    CouponResponseDto createCoupon(String memberUuid, CouponRequestDto requestDto);
    Page<CouponResponseDto> searchCoupon(LocalDateTime startDate, LocalDateTime endDate, String name, Pageable pageable);
    CouponResponseDto getCouponInfo(String memberUuid, String couponUuid);
    CouponResponseDto updateCoupon(String memberUuid, String couponUuid ,CouponUpdateDto updateDto);
    void deleteCoupon(String memberUuid, String couponUuid);
}

