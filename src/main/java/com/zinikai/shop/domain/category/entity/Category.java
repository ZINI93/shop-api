package com.zinikai.shop.domain.category.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.category.dto.CategoryResponseDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "categories")
@Entity
public class Category extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Long id;

    @Column(name = "category_uuid", updatable = false, nullable = false)
    private String categoryUuid;

    @Column(name = "owner_uuid", updatable = false, nullable = false)
    private String ownerUuid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "sort_order")
    private Integer sortOrder;


    @Builder
    public Category(String categoryUuid, String ownerUuid, String name, String slug, Category parent, Integer sortOrder) {
        this.categoryUuid = UUID.randomUUID().toString();
        this.ownerUuid = ownerUuid;
        this.name = name;
        this.slug = slug;
        this.parent = parent;
        this.sortOrder = sortOrder;
    }



    public CategoryResponseDto toResponseDto(){

        return CategoryResponseDto.builder()
                .categoryUuid(this.categoryUuid)
                .ownerUuid(this.ownerUuid)
                .name(this.name)
                .slug(this.slug)
                .parent(this.parent)
                .isActive(this.isActive)
                .sortOrder(this.sortOrder)
                .build();
    }
}
