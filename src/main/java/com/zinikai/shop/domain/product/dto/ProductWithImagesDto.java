package com.zinikai.shop.domain.product.dto;

import com.zinikai.shop.domain.product.entity.ProductCondition;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductWithImagesDto {

    private String name;
    private BigDecimal price;
    private String description;
    private Integer stock;
    private ProductCondition productCondition;
    private String productMaker;
    private String productUuid;
    private List<String> productImages;

    @Builder
    public ProductWithImagesDto(String name, BigDecimal price, String description, Integer stock, ProductCondition productCondition, String productMaker, String productUuid, List<String> productImages) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.productCondition = productCondition;
        this.productMaker = productMaker;
        this.productUuid = productUuid;
        this.productImages = productImages;
    }
}
