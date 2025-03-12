package com.zinikai.shop.domain.product.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductRequestDto {

    @NotBlank
    @Size(min = 1 , max = 50, message = "商品名は５０字以内で入力してください" )
    private String name;

    @NotNull
    @Min(value = 1 , message = "値段は1,000,000円以内で入力してください")
    @Max(value = 1000000 , message = "値段は1,000,000円以内で入力してください")
    private BigDecimal price;

    @NotBlank
    @Size(min = 1 , max = 200, message = "商品の内容は２００字以内で入力してください" )
    private String description;

    @Min(value = 1 , message = "在庫は1000個以内で入力してください")
    @Max(value = 1000000 , message = "在庫は1000個以内で入力してください")
    private Integer stock;

    @Builder
    public ProductRequestDto(String name, BigDecimal price, String description, Integer stock) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
    }
}
