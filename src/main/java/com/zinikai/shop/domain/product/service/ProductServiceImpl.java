package com.zinikai.shop.domain.product.service;

import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.product.dto.ProductRequestDto;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import com.zinikai.shop.domain.product.dto.ProductUpdateDto;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;


    @Override
    @Transactional
    public ProductResponseDto createProduct(Long memberId, ProductRequestDto requestDto) {

        log.info("Creating product for member ID: {}", memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Not found member ID"));

        if (requestDto.getStock() <= 0) {
            throw new IllegalArgumentException("Stock must be greater than 0");
        }

        Product savedProduct = Product.builder()
                .name(requestDto.getName())
                .price(requestDto.getPrice())
                .description(requestDto.getDescription())
                .stock(requestDto.getStock())
                .ownerUuid(member.getMemberUuid())
                .build();

        log.info("created product: {}", savedProduct);

        return productRepository.save(savedProduct).toResponseDto();

    }

    // 商品 - keyword, price 範囲, search logic
    @Override
    public Page<ProductResponseDto> searchProducts(String ownerUuid, String keyword, BigDecimal minPrice, BigDecimal maxPrice, String sortField, Pageable pageable) {

        log.info("Searching product for owner UUID:{}", ownerUuid);

        return productRepository.searchProduct(ownerUuid, keyword, minPrice, maxPrice, sortField, pageable);
    }

    @Override
    public ProductResponseDto getProduct(String ownerUuid, String productUuid) {
        Product product = productRepository.findByOwnerUuidAndProductUuid(ownerUuid, productUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found owner UUID or product UUID"));

        if (Objects.equals(product.getOwnerUuid(), ownerUuid)){
            throw new IllegalArgumentException("Product not match for owner UUID");
        }

        return product.toResponseDto();
    }


    @Override
    @Transactional
    public ProductResponseDto updateProduct(String ownerUuid, String productUuid, ProductUpdateDto updateDto) {

        log.info("Updating product for member UUID :{}, product UUID :{} ", ownerUuid, productUuid);

        Product product = productRepository.findByOwnerUuidAndProductUuid(ownerUuid, productUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found owner UUID or product UUID"));

        matchOwnerUuidAndProductUuid(ownerUuid, productUuid, product);

        product.updateInfo(
                updateDto.getName(),
                updateDto.getPrice(),
                updateDto.getDescription(),
                updateDto.getStock()
        );

        log.info("updated product :{}",product);

        return product.toResponseDto();
    }


    @Override
    @Transactional
    public void deleteProduct(String ownerUuid, String productUuid) {

        log.info("Deleting product for member UUID: {}, product UUID: {}", ownerUuid, productUuid);

        Product product = productRepository.findByOwnerUuidAndProductUuid(ownerUuid, productUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found Owner UUID or Product UUID"));

        matchOwnerUuidAndProductUuid(ownerUuid,productUuid,product);

        productRepository.delete(product);
    }

    private static void matchOwnerUuidAndProductUuid(String ownerUuid, String productUuid, Product product) {
        if (!Objects.equals(product.getOwnerUuid(), ownerUuid)) {
            throw new IllegalArgumentException("Owner UUID does not match the product owner");
        }
        if (!Objects.equals(product.getProductUuid(), productUuid)) {
            throw new IllegalArgumentException("Product UUID does not match");
        }
    }
}
