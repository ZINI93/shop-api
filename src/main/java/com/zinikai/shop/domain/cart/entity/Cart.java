package com.zinikai.shop.domain.cart.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.cart.dto.CartRequestDto;
import com.zinikai.shop.domain.cart.dto.CartResponseDto;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UniqueMemberProduct", columnNames = {"member_id", "product_id"})
})
public class Cart extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Builder
    public Cart(Long id, Member member, Product product, Integer quantity) {
        this.id = id;
        this.member = member;
        this.product = product;
        this.quantity = quantity;
    }

    public CartResponseDto toResponse() {
        return CartResponseDto.builder()
                .id(this.id)
                .memberId(this.member.getId())
                .productId(this.product.getId())
                .quantity(this.quantity)
                .build();
    }

    public void updateInfo(Integer quantity) {
        this.quantity = quantity;
    }
}
