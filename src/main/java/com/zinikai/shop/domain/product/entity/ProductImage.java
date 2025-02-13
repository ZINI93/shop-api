package com.zinikai.shop.domain.product.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.product.dto.ProductImageResponseDto;
import com.zinikai.shop.domain.product.repository.ProductImageRepository;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private String imageUrl;

    @Builder
    public ProductImage(Long id, Product product, String imageUrl) {
        this.id = id;
        this.product = product;
        this.imageUrl = imageUrl;
    }

    public ProductImageResponseDto toResponse() {
        return ProductImageResponseDto.builder()
                .id(this.id)
                .product(this.product)
                .imageUrl(this.imageUrl)
                .build();
    }

    public void updateInfo(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
