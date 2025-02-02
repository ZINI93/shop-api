package com.zinikai.shop.domain.payment.dto;

import com.zinikai.shop.domain.payment.entity.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequestDto {

    @NotNull
    private Long orderId;

    @NotBlank(message = "お支払いのステータスが必要です。")
    private PaymentStatus status;

    @NotBlank(message = "お支払いの方法が必要です。")
    private String paymentMethod;


    @Builder
    public PaymentRequestDto(Long orderId, PaymentStatus status, String paymentMethod) {
        this.orderId = orderId;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }
}
