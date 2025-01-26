package com.zinikai.shop.domain.product.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductRequestDto {

    private String name;
    private BigDecimal price;
    private String description;
    private Integer stock;

    @Builder
    public ProductRequestDto(String name, BigDecimal price, String description, Integer stock) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
    }
}
