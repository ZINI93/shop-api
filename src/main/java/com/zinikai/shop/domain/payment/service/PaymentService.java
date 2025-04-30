package com.zinikai.shop.domain.payment.service;


import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.payment.dto.PaymentRequestDto;
import com.zinikai.shop.domain.payment.dto.PaymentResponseDto;
import com.zinikai.shop.domain.payment.dto.PaymentUpdateDto;
import com.zinikai.shop.domain.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentService {

    PaymentResponseDto createPayment(Member member, Orders orders);

    PaymentResponseDto confirmPayment(String ownerUuid, String paymentUuid);
    PaymentResponseDto cancelPayment(String ownerUuid, String paymentUuid);

    Page<PaymentResponseDto> getPayments(String ownerUuid, Pageable pageable);
    PaymentResponseDto getPayment(String ownerUuid, String paymentUuid);
    PaymentResponseDto updatePayment(String ownerUuid, String paymentUuid , PaymentUpdateDto updateDto);
    void deletePayment(String ownerUuid, String paymentUuid);
    void autoCancelPendingPayments();
    void validatePaymentMethod(String paymentMethod);
}
