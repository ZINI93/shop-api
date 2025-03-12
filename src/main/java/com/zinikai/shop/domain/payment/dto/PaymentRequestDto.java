package com.zinikai.shop.domain.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequestDto {

    @NotNull
    private Long orderId;

    @Builder
    public PaymentRequestDto(Long orderId) {
        this.orderId = orderId;
    }
}
