package com.zinikai.shop.domain.product.dto;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.springframework.data.web.ProjectedPayload;

import java.math.BigDecimal;

@Data
public class ProductResponseDto {

    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private Integer stock;

    @Builder
    @QueryProjection
    public ProductResponseDto(Long id, String name, BigDecimal price, String description, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
    }


}
