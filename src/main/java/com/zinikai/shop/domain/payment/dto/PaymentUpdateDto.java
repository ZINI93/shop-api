package com.zinikai.shop.domain.payment.dto;

import com.zinikai.shop.domain.payment.entity.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentUpdateDto {

    @NotBlank(message = "お支払いのステータスが必要です。")
    private PaymentStatus status;

    @NotBlank(message = "お支払いの方法が必要です。")
    private String paymentMethod;

    @Builder
    public PaymentUpdateDto(PaymentStatus status, String paymentMethod) {
        this.status = status;
        this.paymentMethod = paymentMethod;
    }
}
