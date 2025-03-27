package com.zinikai.shop.domain.payment.repository;

import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.payment.dto.PaymentResponseDto;
import com.zinikai.shop.domain.payment.entity.Payment;
import com.zinikai.shop.domain.payment.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findAllByOwnerUuid(String ownerUuid, Pageable pageable);

    Optional<Payment> findByOwnerUuid(String ownerUuid);

    Optional<Payment> findByPaymentUuid(String paymentUuid);

    Optional<Payment> findByOwnerUuidAndPaymentUuid(String ownerUuid,String paymentUuid);

    Boolean existsByOrdersAndStatus(Orders orders, PaymentStatus paymentStatus);


    List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime expirationTime);
}
