package com.zinikai.shop.domain.product.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Product extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "product_uuid", nullable = false, updatable = false, unique = true)
    private String productUuid;

    @Column(name = "owner_uuid", nullable = false, updatable = false)
    private String ownerUuid;

    @Builder
    public Product(String name, BigDecimal price, String description, Integer stock, String productUuid, String ownerUuid) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.productUuid = UUID.randomUUID().toString();
        this.ownerUuid = ownerUuid;
    }

    public ProductResponseDto toResponseDto() {

        return ProductResponseDto.builder()
                .name(this.name)
                .price(this.price)
                .description(this.description)
                .stock(this.stock)
                .build();
    }

    //アップデート
    public void updateInfo(String name, BigDecimal price, String description, Integer stock) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
    }

    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new IllegalStateException("Stock shortage: " + this.name);
        }
        this.stock -= quantity;
    }

    public void refundStock(int quantity) {
        if (quantity < 1){
            throw new IllegalArgumentException("Refund quantity must be at least 1");
        }
            this.stock += quantity;
    }

}


