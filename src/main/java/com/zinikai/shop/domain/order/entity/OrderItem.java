package com.zinikai.shop.domain.order.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.order.dto.OrderItemResponseDto;
import com.zinikai.shop.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class OrderItem extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id", nullable = false, unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Orders orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;


    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "owner_uuid", nullable = false, updatable = false)
    private String ownerUuid;

    @Column(name = "seller_uuid", nullable = false, updatable = false)
    private String sellerUuid;

    @Column(name = "order_item_uuid", nullable = false, updatable = false, unique = true)
    private String orderItemUuid;

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    @Builder
    public OrderItem(Orders orders, Product product, Integer quantity, BigDecimal price, String ownerUuid, String orderItemUuid, String sellerUuid) {
        this.orders = orders;
        this.product = product;
        this.quantity = quantity;
        this.price = (price != null) ? price : product.getPrice();
        this.ownerUuid = ownerUuid;
        this.orderItemUuid = UUID.randomUUID().toString();
        this.sellerUuid = product.getMember().getMemberUuid();
    }

    public OrderItemResponseDto toResponseDto() {

        return OrderItemResponseDto.builder()
                .orderId(this.getOrders().getId())
                .productId(this.getProduct().getId())
                .quantity(this.getQuantity())
                .price(this.getPrice())
                .sellerUuid(this.sellerUuid)
                .build();
    }
}
