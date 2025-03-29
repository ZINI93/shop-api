package com.zinikai.shop.domain.cart.dto;

import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartResponseDto {

    private Integer quantity;

    @Builder
    public CartResponseDto(Integer quantity) {
        this.quantity = quantity;
    }
}
