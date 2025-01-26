package com.zinikai.shop.domain.payment.dto;

import com.zinikai.shop.domain.payment.entity.PaymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequestDto {

    private Long orderId;
    private PaymentStatus status;
    private String paymentMethod;


    @Builder
    public PaymentRequestDto(Long orderId, PaymentStatus status, String paymentMethod) {
        this.orderId = orderId;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }
}
