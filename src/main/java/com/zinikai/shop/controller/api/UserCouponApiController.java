package com.zinikai.shop.controller.api;

import com.zinikai.shop.domain.coupon.dto.UserCouponResponseDto;
import com.zinikai.shop.domain.coupon.service.UserCouponService;
import com.zinikai.shop.domain.member.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@RestController
public class UserCouponApiController {

    private final UserCouponService userCouponService;


    @PostMapping("{couponUuid}")
    ResponseEntity<UserCouponResponseDto> couponIssuance(@PathVariable String couponUuid,
                                                         Authentication authentication) {
        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        UserCouponResponseDto userCoupon = userCouponService.couponIssuance(memberUuid, couponUuid);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{userCouponUuid}")
                .buildAndExpand(userCoupon.getUserCouponUuid())
                .toUri();

        return ResponseEntity.created(location).body(userCoupon);
    }

    @GetMapping("{userCouponUuid}")
    ResponseEntity<UserCouponResponseDto> getCouponInfo(@PathVariable String userCouponUuid,
                                                        Authentication authentication) {

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        UserCouponResponseDto userCoupon = userCouponService.getCouponInfo(memberUuid, userCouponUuid);

        return ResponseEntity.ok(userCoupon);
    }

    @GetMapping("/me")
    ResponseEntity<Page<UserCouponResponseDto>> userCouponList(@PageableDefault(size = 10) Pageable pageable,
                                                               Authentication authentication) {

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        Page<UserCouponResponseDto> userCoupon = userCouponService.myAllCouponList(memberUuid, pageable);

        return ResponseEntity.ok(userCoupon);
    }

    @GetMapping("/me/unused")
    ResponseEntity<Page<UserCouponResponseDto>> unusedUserCouponList(@PageableDefault(size = 10) Pageable pageable,
                                                                     Authentication authentication) {

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        Page<UserCouponResponseDto> userCoupon = userCouponService.myUnusedCouponList(memberUuid, pageable);

        return ResponseEntity.ok(userCoupon);
    }

    @GetMapping("/me/used")
    ResponseEntity<Page<UserCouponResponseDto>> usedUserCouponList(@PageableDefault(size = 10) Pageable pageable,
                                                                   Authentication authentication) {

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        Page<UserCouponResponseDto> userCoupon = userCouponService.myUsedCouponList(memberUuid, pageable);

        return ResponseEntity.ok(userCoupon);
    }

    @DeleteMapping("{userCouponUuid}")
    ResponseEntity<Void> deleteUserCoupon(@PathVariable String userCouponUuid,
                                          Authentication authentication) {

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        userCouponService.deleteCoupon(memberUuid, userCouponUuid);

        return ResponseEntity.noContent().build();
    }

    private CustomUserDetails getCustomUserDetails(Authentication authentication) {
        return (CustomUserDetails) authentication.getPrincipal();
    }
}
