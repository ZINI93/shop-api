package com.zinikai.shop.domain.coupon.service;

import com.zinikai.shop.domain.coupon.dto.CouponRequestDto;
import com.zinikai.shop.domain.coupon.dto.CouponResponseDto;
import com.zinikai.shop.domain.coupon.dto.CouponUpdateDto;
import com.zinikai.shop.domain.coupon.entity.Coupon;
import com.zinikai.shop.domain.coupon.entity.DiscountType;
import com.zinikai.shop.domain.coupon.repository.CouponRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

    @Mock MemberRepository memberRepository;
    @Mock CouponRepository couponRepository;
    @InjectMocks CouponServiceImpl couponService;
    Member member;

    Coupon coupon;
    CouponRequestDto requestDto;


    @BeforeEach
    void setup(){

        member = Member.builder().memberUuid(UUID.randomUUID().toString()).build();


        requestDto = CouponRequestDto.builder()
                .name("shop_coupon")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(5))
                .minOrderAmount(new BigDecimal("1000.00"))
                .discountValue(new BigDecimal("1000.00"))
                .discountType(DiscountType.PERCENTAGE)
                .description("coupon 10%")
                .maxUsage(1)
                .build();

        coupon = new Coupon(
                UUID.randomUUID().toString(),
                member.getMemberUuid(),
                requestDto.getName(),
                requestDto.getStartDate(),
                requestDto.getEndDate(),
                requestDto.getMinOrderAmount(),
                requestDto.getDiscountValue(),
                requestDto.getDiscountType(),
                requestDto.getDescription(),
                requestDto.getMaxUsage()
        );

    }

    @Test
    void createCoupon() {
        //given
        when(memberRepository.findByMemberUuid(member.getMemberUuid())).thenReturn(Optional.ofNullable(member));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        //when
        CouponResponseDto result = couponService.createCoupon(member.getMemberUuid(), requestDto);

        //then
        assertNotNull(result);
        assertEquals(requestDto.getDiscountValue(),result.getDiscountValue());
        assertEquals(requestDto.getName(),result.getName());

        verify(couponRepository,times(1)).save(any(Coupon.class));
    }

    @Test
    void getCouponList() {
        //given
        PageRequest pageable = PageRequest.of(0, 10);

        List<Coupon> mockCoupons = List.of(coupon);
        PageImpl<Coupon> mockPage = new PageImpl<>(mockCoupons, pageable, mockCoupons.size());

        when(couponRepository.findByCoupons(coupon.getStartDate(),coupon.getEndDate(),coupon.getName(),pageable)).thenReturn(mockPage);

        //when
        Page<CouponResponseDto> result = couponService.searchCoupon(coupon.getStartDate(), coupon.getEndDate(), coupon.getName(), pageable);

        //then
        assertNotNull(result);
        assertEquals(10,result.getSize());

        verify(couponRepository, times(1)).findByCoupons(
                coupon.getStartDate(),
                coupon.getEndDate(),
                coupon.getName(),
                pageable
        );
    }

    @Test
    void getCouponInfo() {
        //given

        when(couponRepository.findByMemberMemberUuidAndCouponUuid(member.getMemberUuid(),coupon.getCouponUuid())).thenReturn(Optional.ofNullable(coupon));

        //when
        CouponResponseDto result = couponService.getCouponInfo(member.getMemberUuid(), coupon.getCouponUuid());

        //then
        assertNotNull(result);
        assertEquals(coupon.getName(),result.getName());

        verify(couponRepository,times(1)).findByMemberMemberUuidAndCouponUuid(member.getMemberUuid(),coupon.getCouponUuid());

    }

    @Test
    void updateCoupon() {
        //given
        when(couponRepository.findByMemberMemberUuidAndCouponUuid(member.getMemberUuid(),coupon.getCouponUuid())).thenReturn(Optional.ofNullable(coupon));
        CouponUpdateDto updateDto = new CouponUpdateDto(LocalDateTime.now(),LocalDateTime.now().plusDays(5),new BigDecimal("1000.00"),
                new BigDecimal("1000.00"),DiscountType.FIXED_AMOUNT,"zini-shop",3);

        //when
        coupon.updateCoupon(updateDto.getStartDate(),updateDto.getEndDate(),updateDto.getMinOrderAmount(),updateDto.getDiscountValue(),updateDto.getDiscountType(),updateDto.getDescription(),updateDto.getMaxUsage());
        CouponResponseDto result = couponService.updateCoupon(member.getMemberUuid(), coupon.getCouponUuid(), updateDto);


        //then
        assertNotNull(result);
        assertEquals(updateDto.getStartDate(),result.getStartDate());
        assertEquals(updateDto.getEndDate(),result.getEndDate());

        verify(couponRepository, times(1)).findByMemberMemberUuidAndCouponUuid(member.getMemberUuid(),coupon.getCouponUuid());
    }

    @Test
    void deleteCoupon() {
        //given
        when(couponRepository.findByMemberMemberUuidAndCouponUuid(member.getMemberUuid(),coupon.getCouponUuid())).thenReturn(Optional.ofNullable(coupon));

        //when
        couponService.deleteCoupon(member.getMemberUuid(),coupon.getCouponUuid());

        //then
        verify(couponRepository, times(1)).findByMemberMemberUuidAndCouponUuid(member.getMemberUuid(),coupon.getCouponUuid());
    }
}