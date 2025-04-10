package com.zinikai.shop.domain.product.entity;

import com.zinikai.shop.domain.TimeStamp;
import com.zinikai.shop.domain.product.dto.ProductImageResponseDto;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import com.zinikai.shop.domain.product.dto.ProductWithImagesDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;



@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_images")
@Entity
public class ProductImage extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private String imageUrl;

    @Column(name = "owner_uuid", nullable = false)
    private String ownerUuid;

    @Column(name = "product_image_uuid", nullable = false, updatable = false, unique = true)
    private String productImageUuid;

    @Builder
    public ProductImage(Product product, String imageUrl, String ownerUuid, String productImageUuid) {
        this.product = product;
        this.imageUrl = imageUrl;
        this.ownerUuid = ownerUuid;
        this.productImageUuid = UUID.randomUUID().toString();
    }

    public ProductImageResponseDto toResponse() {
        return ProductImageResponseDto.builder()
                .imageUrl(this.imageUrl)
                .productImageUuid(this.getProductImageUuid())
                .build();
    }

    public void updateInfo(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
