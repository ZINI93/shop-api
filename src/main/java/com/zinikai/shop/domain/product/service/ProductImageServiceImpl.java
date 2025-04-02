package com.zinikai.shop.domain.product.service;

import com.zinikai.shop.domain.product.dto.*;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.entity.ProductCondition;
import com.zinikai.shop.domain.product.entity.ProductImage;
import com.zinikai.shop.domain.product.repository.ProductImageRepository;
import com.zinikai.shop.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;


    @Override
    public List<ProductImageResponseDto> getImagesInProduct(String productUuid) {

        List<ProductImage> images = productImageRepository.findAllByProductProductUuid(productUuid);

        if (images.isEmpty()) {
            throw new IllegalArgumentException("Not found product image");
        }

        return images.stream().map(ProductImage::toResponse).collect(Collectors.toList());
    }

    @Override
    public Page<ProductImageResponseDto> getImagesByMember(String ownerUuid, Pageable pageable) {

        log.info("Searching productImage for owner UUID:{}", ownerUuid);

        return productImageRepository.findAllByOwnerUuid(ownerUuid, pageable);
    }

    @Override
    @Transactional
    public ProductImageResponseDto updateProductImage(String ownerUuid, String productImageUuid, ProductImageUpdateDto updateDto) {

        log.info("Updating productImage for member UUID:{}, productImage UUID:{}", ownerUuid, productImageUuid);

        ProductImage productImage = productImageRepository.findByOwnerUuidAndProductImageUuid(ownerUuid, productImageUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found Owner UUID: " + ownerUuid + "ProductImage UUID:" + productImageUuid));

        matchOwnerUuid(ownerUuid, productImage);


        productImage.updateInfo(updateDto.getImageUrl());

        log.info("updated productImage:{}", productImage);

        return productImage.toResponse();
    }

    @Override
    public ProductWithImagesDto getProductWithImages(String productUuid) {

        Product product = productRepository.findByProductUuid(productUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found product UUID"));

        List<ProductImage> productImagesByProductUuid = productImageRepository.findProductImagesByProductUuid(productUuid);

        List<String> imageUrls = productImagesByProductUuid.stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());

        return new ProductWithImagesDto(product.getName(), product.getPrice(), product.getDescription(), product.getStock(), ProductCondition.NEW, product.getProductMaker(),
                productUuid, imageUrls);

    }


    @Override
    @Transactional
    public void deleteProductImage(String ownerUuid, String productImageUuid) {

        log.info("deleting productImage for member UUID:{}, productImage UUID:{}", ownerUuid, productImageUuid);

        ProductImage productImage = productImageRepository.findByOwnerUuidAndProductImageUuid(ownerUuid, productImageUuid)
                .orElseThrow(() -> new IllegalArgumentException("Owner UUID does not match the productImage owner"));

        matchOwnerUuid(ownerUuid, productImage);

        productImageRepository.delete(productImage);
    }

    private static void matchOwnerUuid(String ownerUuid, ProductImage productImage) {
        if (!Objects.equals(productImage.getOwnerUuid(), ownerUuid)) {
            throw new IllegalArgumentException("Owner UUID does not match ProductImage owner");
        }
    }
}
