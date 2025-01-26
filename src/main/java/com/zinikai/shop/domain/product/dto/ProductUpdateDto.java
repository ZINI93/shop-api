package com.zinikai.shop.domain.product.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductUpdateDto {

    private String name;
    private BigDecimal price;
    private String description;
    private Integer stock;

    public ProductUpdateDto(String name, BigDecimal price, String description, Integer stock) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
    }
}
