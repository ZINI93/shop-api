package com.zinikai.shop.domain.coupon.repository;

import com.zinikai.shop.domain.coupon.dto.CouponResponseDto;
import com.zinikai.shop.domain.coupon.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByOwnerUuid(String ownerUuid);
    Optional<Coupon> findByCouponUuid(String couponUuid);
    Optional<Coupon> findByOwnerUuidAndCouponUuid(String ownerUuid, String couponUuid);


    @Query("SELECT c FROM Coupon c " +
            "WHERE (:name IS NULL OR c.name = :name) " +
            "AND ( " +
            "  (c.startDate <= :endDate AND c.endDate >= :startDate) " +
            ") " +
            "ORDER BY c.createdAt")
    Page<Coupon> findByCoupons(@Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate,
                              @Param("name") String name,
                              Pageable pageable);
}