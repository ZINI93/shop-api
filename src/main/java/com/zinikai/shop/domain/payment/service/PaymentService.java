package com.zinikai.shop.domain.payment.service;


import com.zinikai.shop.domain.payment.dto.PaymentRequestDto;
import com.zinikai.shop.domain.payment.dto.PaymentResponseDto;
import com.zinikai.shop.domain.payment.dto.PaymentUpdateDto;
import com.zinikai.shop.domain.payment.entity.Payment;

import java.util.List;

public interface PaymentService {

    PaymentResponseDto createPayment(PaymentRequestDto requestDto);
    PaymentResponseDto getPaymentById(Long paymentId);
    List<PaymentResponseDto> getAllPayment();
    PaymentResponseDto updatePayment(Long paymentId, PaymentUpdateDto updateDto);
    void deletePayment(Long paymentId);
}
