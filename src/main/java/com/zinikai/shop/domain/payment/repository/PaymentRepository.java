package com.zinikai.shop.domain.payment.repository;

import com.zinikai.shop.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}