package com.zinikai.shop.domain.order.dto;

import com.zinikai.shop.domain.order.entity.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrdersUpdateDto {

    @NotBlank(message = "注文のステータスが必要です。")
    private Status status;

    @Builder
    public OrdersUpdateDto(BigDecimal totalAmount, Status status, String paymentMethod) {
        this.status = status;
    }
}
