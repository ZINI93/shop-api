package com.zinikai.shop.domain.category.dto;

import com.zinikai.shop.domain.category.entity.Category;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;


@Data
public class CategoryResponseDto {

    private String CategoryUuid;
    private String ownerUuid;
    private String name;
    private String slug;
    private Category parent;
    private Boolean isActive;
    private Integer sortOrder;


    @Builder
    public CategoryResponseDto(String categoryUuid, String ownerUuid, String name, String slug, Category parent, Boolean isActive, Integer sortOrder) {
        CategoryUuid = categoryUuid;
        this.ownerUuid = ownerUuid;
        this.name = name;
        this.slug = slug;
        this.parent = parent;
        this.isActive = isActive;
        this.sortOrder = sortOrder;
    }
}
