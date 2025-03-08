package com.zinikai.shop.domain.product.service;

import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.product.dto.*;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.entity.ProductImage;
import com.zinikai.shop.domain.product.repository.ProductImageRepository;
import com.zinikai.shop.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public ProductImageResponseDto createProductImage(Long memberId, ProductImageRequestDto requestDto) {

        log.info("Creating ProductImage for member ID: {}", memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Not found user ID"));

        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Not found product ID"));


        ProductImage savedProductImage = ProductImage.builder()
                .product(product)
                .imageUrl(requestDto.getImageUrl())
                .ownerUuid(member.getMemberUuid())
                .build();

        log.info("Created product : {}", savedProductImage);

        return productImageRepository.save(savedProductImage).toResponse();
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
                .orElseThrow(() -> new IllegalArgumentException("該当する商品イメージが見つかりません"));

        matchOwnerUuid(ownerUuid, productImage);


        productImage.updateInfo(updateDto.getImageUrl());

        log.info("updated productImage:{}", productImage);

        return productImage.toResponse();
    }

    @Override
    @Transactional
    public void deleteProductImage(String ownerUuid, String productImageUuid) {

        log.info("deleting productImage for member UUID:{}, productImage UUID:{}", ownerUuid, productImageUuid);

        ProductImage productImage = productImageRepository.findByOwnerUuidAndProductImageUuid(ownerUuid, productImageUuid)
                .orElseThrow(() -> new IllegalArgumentException("Owner UUID does not match the productImage owner"));

        matchOwnerUuid(ownerUuid,productImage);

        productImageRepository.delete(productImage);
    }

    private static void matchOwnerUuid(String ownerUuid, ProductImage productImage) {
        if (!Objects.equals(productImage.getOwnerUuid(), ownerUuid)){
            throw new IllegalArgumentException("Owner UUID does not match ProductImage owner");
        }
    }
}
