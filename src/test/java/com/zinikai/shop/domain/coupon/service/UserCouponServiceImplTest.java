package com.zinikai.shop.domain.coupon.service;

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

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCouponServiceImplTest {

    @Mock MemberRepository memberRepository;
    @Mock CouponRepository couponRepository;
    @Mock UserCouponRepository userCouponRepository;
    @Mock private Clock clock;
    private final Instant fixedInstant = Instant.parse("2025-01-01T12:00:00Z");

    @InjectMocks UserCouponServiceImpl userCouponService;

    Member member;
    Coupon coupon;
    UserCoupon userCoupon;

    Clock fixedClock;

    @BeforeEach
    void setup() {

        fixedClock = Clock.fixed(
                Instant.parse("2025-01-01T12:00:00Z"),
                ZoneId.systemDefault()
        );

        member = Member.builder().memberUuid(UUID.randomUUID().toString()).build();
        coupon = Coupon.builder().member(member).couponUuid(UUID.randomUUID().toString()).startDate(LocalDateTime.now().minusDays(2)).build();

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

        verify(memberRepository,times(1)).findByMemberUuid(member.getMemberUuid());
        verify(couponRepository,times(1)).findByCouponUuid(coupon.getCouponUuid());
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

        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(userCouponRepository.findAllUserCoupons(
                eq(member.getMemberUuid()),
                any(LocalDateTime.class),
                        eq(pageable)))
                .thenReturn(mockPage);

        //when
        Page<UserCouponResponseDto> result = userCouponService.myAllCouponList(member.getMemberUuid(), pageable);

        //then
        assertNotNull(result);
        assertEquals(mockPage.getSize(),result.getSize());

        verify(userCouponRepository, times(1)).findAllUserCoupons(eq(member.getMemberUuid()),any(LocalDateTime.class),eq(pageable));

    }

    @Test
    void unusedCouponListTest(){

        //given

        LocalDateTime now = LocalDateTime.now(fixedClock);

        PageRequest pageable = PageRequest.of(0, 10);
        List<UserCoupon> mockData = List.of(userCoupon);
        PageImpl<UserCoupon> userCoupons = new PageImpl<>(mockData, pageable, mockData.size());

        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(userCouponRepository.findUsableCoupons(member.getMemberUuid(), now, pageable)).thenReturn(userCoupons);


        //when

        Page<UserCouponResponseDto> result = userCouponService.myUnusedCouponList(member.getMemberUuid(), pageable);

        //then
        assertNotNull(result);
        assertEquals(userCoupons.getSize(),result.getSize());
        assertEquals(false, result.getContent().get(0).getIsUsed() );

        verify(userCouponRepository,times(1)).findUsableCoupons(member.getMemberUuid(), now, pageable);

    }

    @Test
    void usedUserCouponListTest(){

        //given

        UserCoupon usedCoupon = UserCoupon.builder().member(member).coupon(coupon).isUsed(true).build();

        PageRequest pageable = PageRequest.of(0, 10);
        List<UserCoupon> mockData = List.of(usedCoupon);
        PageImpl<UserCoupon> userCoupons = new PageImpl<>(mockData, pageable, mockData.size());

        LocalDateTime now = LocalDateTime.now(fixedClock);

        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(userCouponRepository.findUsedCoupons(member.getMemberUuid(),now,pageable)).thenReturn(userCoupons);

        //when

        Page<UserCouponResponseDto> result = userCouponService.myUsedCouponList(member.getMemberUuid(), pageable);

        //then
        assertNotNull(result);
        assertEquals(true, result.getContent().get(0).getIsUsed());

        verify(userCouponRepository,times(1)).findUsedCoupons(member.getMemberUuid(),now,pageable);

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