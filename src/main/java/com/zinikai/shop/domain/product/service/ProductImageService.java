package com.zinikai.shop.domain.product.service;

import com.zinikai.shop.domain.product.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductImageService {

    List<ProductImageResponseDto> getImagesInProduct(String productUuid);

    Page<ProductImageResponseDto> getImagesByMember(String ownerUuid, Pageable pageable);

    //    ProductImageResponseDto updateProductImage(Long productImageId, ProductImageUpdateDto updateDto);
    ProductImageResponseDto updateProductImage(String ownerUuid, String productImageUuid, ProductImageUpdateDto updateDto);

    ProductWithImagesDto getProductWithImages(String productUuid);

    void deleteProductImage(String ownerUuid, String productImageUuid);
}
