package com.zinikai.shop.domain.cart.dto;

import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartResponseDto {

    private Long id;
    private Long memberId ;
    private Long productId;
    private Integer quantity;

    @Builder
    public CartResponseDto(Long id, Long memberId, Long productId, Integer quantity) {
        this.id = id;
        this.memberId = memberId;
        this.productId = productId;
        this.quantity = quantity;
    }
}
