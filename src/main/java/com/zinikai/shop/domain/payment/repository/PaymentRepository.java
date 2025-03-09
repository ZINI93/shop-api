package com.zinikai.shop.domain.payment.repository;

import com.zinikai.shop.domain.payment.dto.PaymentResponseDto;
import com.zinikai.shop.domain.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findAllByOwnerUuid(String ownerUuid, Pageable pageable);

    Optional<Payment> findByOwnerUuid(String ownerUuid);

    Optional<Payment> findByPaymentUuid(String paymentUuid);

    Optional<Payment> findByOwnerUuidAndPaymentUuid(String ownerUuid,String paymentUuid);

}
