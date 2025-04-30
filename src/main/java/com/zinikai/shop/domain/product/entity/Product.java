package com.zinikai.shop.domain.product.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import com.zinikai.shop.domain.product.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    private Member member;

    @Builder
    public Product(String name, BigDecimal price, String description, Integer stock, ProductStatus productStatus, ProductCondition productCondition, String productMaker, String productUuid, Member member) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.productStatus = productStatus;
        this.productCondition = productCondition;
        this.productMaker = productMaker;
        this.productUuid = UUID.randomUUID().toString();
        this.member = member;
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
            throw new OutOfStockException("request quantity" + quantity + "remaining stock" + this.stock);
        }
        this.stock -= quantity;
    }

    public void refundStock(int quantity) {
        if (quantity < 1) {
            throw new OutOfStockException("Refund quantity must be at least 1");
        }
        this.stock += quantity;
    }

}


