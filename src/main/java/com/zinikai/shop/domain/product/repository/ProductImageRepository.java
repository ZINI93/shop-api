package com.zinikai.shop.domain.product.repository;

import com.zinikai.shop.domain.product.dto.ProductImageResponseDto;
import com.zinikai.shop.domain.product.entity.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    Optional<ProductImage> findByOwnerUuidAndProductImageUuid(String ownerUuid,String productUuid);

    Page<ProductImageResponseDto> findAllByOwnerUuid(String ownerUuid, Pageable pageable);
}
