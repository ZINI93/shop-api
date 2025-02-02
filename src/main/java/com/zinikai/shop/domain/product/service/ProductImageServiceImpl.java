package com.zinikai.shop.domain.product.service;

import com.zinikai.shop.domain.product.dto.*;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.entity.ProductImage;
import com.zinikai.shop.domain.product.repository.ProductImageRepository;
import com.zinikai.shop.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    //イメージ생서
    @Override @Transactional
    public ProductImageResponseDto createProductImage(ProductImageRequestDto requestDto) {
        Product productId = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("商品がありません。"));

        ProductImage savedProductImage = ProductImage.builder()
                .product(productId)
                .imageUrl(requestDto.getImageUrl())
                .build();
        return productImageRepository.save(savedProductImage).toResponse();
    }

    //idでイメージを探す
    @Override
    public ProductImageResponseDto getProductImageById(Long productImageId) {
        ProductImage productImage = productImageRepository.findById(productImageId)
                .orElseThrow(() -> new IllegalArgumentException("イメージが見つかりません"));
        return productImage.toResponse();
    }

    //イメージのurl アップデート
    @Override @Transactional
    public ProductImageResponseDto updateProductImage(Long productImageId, ProductImageUpdateDto updateDto) {
        ProductImage productImage = productImageRepository.findById(productImageId)
                .orElseThrow(() -> new IllegalArgumentException("イメージが見つかりません"));

        productImage.updateInfo(
                updateDto.getImageUrl()
        );

        return productImageRepository.save(productImage).toResponse();
    }
    //イメージを削除
    @Override
    public void deleteProductImage(Long productImageId) {
        if (!productImageRepository.existsById(productImageId)){
            throw new EntityNotFoundException("イメージが見つかりません");
        }
        productImageRepository.deleteById(productImageId);
    }
}
