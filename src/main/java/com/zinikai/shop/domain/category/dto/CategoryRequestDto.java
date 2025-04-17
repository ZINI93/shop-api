package com.zinikai.shop.domain.category.dto;



import lombok.Builder;
import lombok.Data;

@Data
public class CategoryRequestDto {

    private String name;
    private String slug;
    private String parentUuid;
    private Integer sortOrder;


    @Builder
    public CategoryRequestDto(String name, String slug, String parentUuid, Integer sortOrder) {
        this.name = name;
        this.slug = slug;
        this.parentUuid = parentUuid;
        this.sortOrder = sortOrder;
    }
}
