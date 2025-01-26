package com.zinikai.shop.domain.payment.repository;

import com.zinikai.shop.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}