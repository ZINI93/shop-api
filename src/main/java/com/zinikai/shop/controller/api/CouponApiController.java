package com.zinikai.shop.controller.api;

import com.zinikai.shop.domain.coupon.dto.CouponRequestDto;
import com.zinikai.shop.domain.coupon.dto.CouponResponseDto;
import com.zinikai.shop.domain.coupon.dto.CouponUpdateDto;
import com.zinikai.shop.domain.coupon.service.CouponService;
import com.zinikai.shop.domain.member.service.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;

@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
@RestController
public class CouponApiController {


    private final CouponService couponService;


    @PostMapping
    public ResponseEntity<CouponResponseDto> createCoupon(Authentication authentication,
                                                          @RequestBody @Valid CouponRequestDto requestDto) {

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        CouponResponseDto coupon = couponService.createCouponWithValidate(memberUuid, requestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{couponUuid}")
                .buildAndExpand(coupon.getCouponUuid())
                .toUri();

        return ResponseEntity.created(location).body(coupon);
    }

    @GetMapping("/list")  // x
    public ResponseEntity<Page<CouponResponseDto>> searchCoupon(@PageableDefault(size = 10) Pageable pageable,
                                                                @RequestParam LocalDateTime startDate,
                                                                @RequestParam LocalDateTime endDate,
                                                                @RequestParam String name) {

        Page<CouponResponseDto> coupon = couponService.searchCoupon(startDate, endDate, name, pageable);

        return ResponseEntity.ok(coupon);
    }

    @GetMapping("{couponUuid}")
    public ResponseEntity<CouponResponseDto> getCouponInfo(Authentication authentication,
                                                           @PathVariable String couponUuid) {

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        CouponResponseDto coupon = couponService.getCouponInfo(memberUuid, couponUuid);

        return ResponseEntity.ok(coupon);
    }

    @PutMapping("{couponUuid}")
    public ResponseEntity<CouponResponseDto> updateCoupon(Authentication authentication,
                                                          @PathVariable String couponUuid,
                                                          @RequestBody CouponUpdateDto updateDto) {

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        CouponResponseDto coupon = couponService.updateCoupon(memberUuid, couponUuid, updateDto);

        return ResponseEntity.ok(coupon);
    }

    @DeleteMapping("{couponUuid}")
    public ResponseEntity<Void> deleteCoupon(Authentication authentication,
                                             @PathVariable String couponUuid) {

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        couponService.deleteCoupon(memberUuid, couponUuid);

        return ResponseEntity.noContent().build();
    }


    private static CustomUserDetails getCustomUserDetails(Authentication authentication) {
        return (CustomUserDetails) authentication.getPrincipal();
    }

}
