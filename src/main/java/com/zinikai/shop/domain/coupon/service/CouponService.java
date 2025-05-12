package com.zinikai.shop.domain.coupon.service;

import com.zinikai.shop.domain.coupon.dto.CouponRequestDto;
import com.zinikai.shop.domain.coupon.dto.CouponResponseDto;
import com.zinikai.shop.domain.coupon.dto.CouponUpdateDto;
import com.zinikai.shop.domain.coupon.entity.Coupon;
import com.zinikai.shop.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface CouponService {
    Coupon createCoupon(Member member, CouponRequestDto requestDto);
    CouponResponseDto createCouponWithValidate(String memberUuid , CouponRequestDto requestDto);
    Page<CouponResponseDto> searchCoupon(LocalDateTime startDate, LocalDateTime endDate, String name, Pageable pageable);
    CouponResponseDto getCouponInfo(String memberUuid, String couponUuid);
    CouponResponseDto updateCoupon(String memberUuid, String couponUuid ,CouponUpdateDto updateDto);
    void deleteCoupon(String memberUuid, String couponUuid);
}

