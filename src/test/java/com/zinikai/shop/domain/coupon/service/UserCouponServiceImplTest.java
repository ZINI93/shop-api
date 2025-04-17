package com.zinikai.shop.domain.coupon.service;

import com.zinikai.shop.controller.api.UserCouponApiController;
import com.zinikai.shop.domain.coupon.dto.UserCouponResponseDto;
import com.zinikai.shop.domain.coupon.entity.Coupon;
import com.zinikai.shop.domain.coupon.entity.UserCoupon;
import com.zinikai.shop.domain.coupon.repository.CouponRepository;
import com.zinikai.shop.domain.coupon.repository.UserCouponRepository;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCouponServiceImplTest {

    @Mock
    MemberRepository memberRepository;
    @Mock
    CouponRepository couponRepository;
    @Mock
    UserCouponRepository userCouponRepository;

    @InjectMocks
    UserCouponServiceImpl userCouponService;

    Member member;

    Coupon coupon;

    UserCoupon userCoupon;

    @BeforeEach
    void setup() {

        member = Member.builder().memberUuid(UUID.randomUUID().toString()).build();
        coupon = Coupon.builder().ownerUuid(member.getMemberUuid()).couponUuid(UUID.randomUUID().toString()).startDate(LocalDateTime.now().minusDays(2)).build();

        userCoupon = UserCoupon.builder()
                .member(member)
                .coupon(coupon)
                .usedAt(null)
                .isUsed(false)
                .build();


    }

    @Test
    void couponIssuance() {

        //given
        when(memberRepository.findByMemberUuid(member.getMemberUuid())).thenReturn(Optional.ofNullable(member));
        when(couponRepository.findByCouponUuid(coupon.getCouponUuid())).thenReturn(Optional.ofNullable(coupon));
        when(userCouponRepository.save(any(UserCoupon.class))).thenReturn(userCoupon);

        //when
        UserCouponResponseDto result = userCouponService.couponIssuance(member.getMemberUuid(), coupon.getCouponUuid());

        //then
        assertNotNull(result);

        verify(userCouponRepository, times(1)).save(any(UserCoupon.class));
    }

    @Test
    void getCouponInfo() {

        //when
        when(userCouponRepository.findByMemberMemberUuidAndUserCouponUuid(member.getMemberUuid(), userCoupon.getUserCouponUuid())).thenReturn(Optional.ofNullable(userCoupon));

        //given
        UserCouponResponseDto result = userCouponService.getCouponInfo(member.getMemberUuid(), userCoupon.getUserCouponUuid());

        //then
        assertNotNull(result);

        verify(userCouponRepository, times(1)).findByMemberMemberUuidAndUserCouponUuid(member.getMemberUuid(),userCoupon.getUserCouponUuid());

    }

    @Test
    void myCouponList() {

        //given
        PageRequest pageable = PageRequest.of(0, 10);

        List<UserCoupon> mockUserCoupons = List.of(userCoupon);
        PageImpl<UserCoupon> mockPage = new PageImpl<>(mockUserCoupons, pageable, mockUserCoupons.size());

        LocalDateTime now = LocalDateTime.now();

        when(userCouponRepository.findValidUserCoupons(member.getMemberUuid(),now,pageable)).thenReturn(mockPage);

        //when
        Page<UserCouponResponseDto> result = userCouponService.myCouponList(member.getMemberUuid(), pageable);

        //then
        assertNotNull(result);
        assertEquals(mockUserCoupons.size(),result.getSize());

        verify(userCouponRepository, times(1)).findValidUserCoupons(member.getMemberUuid(),now,pageable);

    }

    @Test
    void deleteCoupon() {
        //given
        when(userCouponRepository.findByMemberMemberUuidAndUserCouponUuid(member.getMemberUuid(), userCoupon.getUserCouponUuid())).thenReturn(Optional.ofNullable(userCoupon));

        //when
        userCouponService.deleteCoupon(member.getMemberUuid(), userCoupon.getUserCouponUuid());

        //then
        verify(userCouponRepository, times(1)).delete(any(UserCoupon.class));

    }
}