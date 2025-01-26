package com.zinikai.shop.domain.product.service;

import com.zinikai.shop.domain.product.dto.*;
import com.zinikai.shop.domain.product.repository.ProductImageRepository;

public interface ProductImageService {

    ProductImageResponseDto createProductImage(ProductImageRequestDto requestDto);

    ProductImageResponseDto getProductImageById(Long productImageId);
    ProductImageResponseDto updateProductImage(Long productImageId, ProductImageUpdateDto updateDto);
    void deleteProductImage(Long productImageId);
}
