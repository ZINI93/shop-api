package com.zinikai.shop.domain.category.dto;

import com.zinikai.shop.domain.category.entity.Category;
import lombok.Builder;
import lombok.Data;

@Data
public class CategoryUpdateDto {

    private String name;
    private String slug;
    private Category parent;
    private Integer sortOrder;


    @Builder
    public CategoryUpdateDto(String name, String slug, Category parent, Integer sortOrder) {
        this.name = name;
        this.slug = slug;
        this.parent = parent;
        this.sortOrder = sortOrder;
    }
}
