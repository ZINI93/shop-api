package com.zinikai.shop.domain.product.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
@Entity
public class Product extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_status", nullable = false)
    private ProductStatus productStatus = ProductStatus.ON_SALE;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_condition", nullable = false)
    private ProductCondition productCondition;

    @Column(name = "product_maker", nullable = false)
    private String productMaker;

    @Column(name = "product_uuid", nullable = false, updatable = false, unique = true)
    private String productUuid;

    @Column(name = "owner_uuid", nullable = false, updatable = false)
    private String ownerUuid;

    @Builder
    public Product(String name, BigDecimal price, String description, Integer stock, ProductStatus productStatus, ProductCondition productCondition, String productMaker, String productUuid, String ownerUuid) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.productStatus = productStatus;
        this.productCondition = productCondition;
        this.productMaker = productMaker;
        this.productUuid = UUID.randomUUID().toString();
        this.ownerUuid = ownerUuid;
    }


    public ProductResponseDto toResponseDto() {

        return ProductResponseDto.builder()
                .name(this.name)
                .price(this.price)
                .description(this.description)
                .stock(this.stock)
                .productCondition(this.productCondition)
                .productMaker(this.productMaker)
                .productUuid(this.getProductUuid())
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
        if (quantity < 1) {
            throw new IllegalArgumentException("Refund quantity must be at least 1");
        }
        this.stock += quantity;
    }

}


