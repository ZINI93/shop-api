package com.zinikai.shop.domain.payment.dto;

import com.zinikai.shop.domain.payment.entity.PaymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentUpdateDto {

    private PaymentStatus status;
    private String paymentMethod;

    @Builder
    public PaymentUpdateDto(PaymentStatus status, String paymentMethod) {
        this.status = status;
        this.paymentMethod = paymentMethod;
    }
}
