package com.zinikai.shop.domain.coupon.repository;

import com.zinikai.shop.domain.coupon.entity.Coupon;
import com.zinikai.shop.domain.coupon.entity.UserCoupon;
import com.zinikai.shop.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    Optional<UserCoupon> findByMemberMemberUuid(String memberUuid);
    Optional<UserCoupon> findByOrderOrderUuid(String orderUuid);

    Optional<UserCoupon> findByMemberMemberUuidAndUserCouponUuid(String memberUuid, String userCouponUuid);

    @Query("select uc from UserCoupon uc " +
            "join fetch uc.coupon c " +
            "join fetch uc.member m " +
            "where m.memberUuid = :memberUuid " +
            "and c.startDate < :now " +
            "and c.endDate > :now")
    Page<UserCoupon> findAllUserCoupons(@Param("memberUuid") String memberUuid, @Param("now") LocalDateTime now, Pageable pageable);


    @Query("select uc from UserCoupon uc " +
            "join fetch uc.coupon c " +
            "join fetch uc.member m " +
            "where m.memberUuid = :memberUuid " +
            "and c.startDate <= :now " +
            "and uc.isUsed = false")
    Page<UserCoupon> findUsableCoupons(@Param("memberUuid") String memberUuid, @Param("now") LocalDateTime now, Pageable pageable);


    @Query("select uc from UserCoupon uc " +
            "join fetch uc.coupon c " +
            "join fetch uc.member m " +
            "where m.memberUuid = :memberUuid " +
            "and c.endDate >= :now " +
            "and uc.isUsed = true")
    Page<UserCoupon> findUsedCoupons(@Param("memberUuid") String memberUuid, @Param("now") LocalDateTime now, Pageable pageable);

    boolean existsByMemberAndCoupon(Member member, Coupon coupon);
}