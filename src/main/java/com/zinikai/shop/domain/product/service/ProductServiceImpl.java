package com.zinikai.shop.domain.product.service;

import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.product.dto.ProductRequestDto;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import com.zinikai.shop.domain.product.dto.ProductUpdateDto;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.entity.ProductImage;
import com.zinikai.shop.domain.product.entity.ProductStatus;
import com.zinikai.shop.domain.product.repository.ProductImageRepository;
import com.zinikai.shop.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public ProductResponseDto createProduct(String memberUuid, ProductRequestDto requestDto) {

        log.info("Creating product for member UUID: {}", memberUuid);

        Member member = memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found member ID"));

        if (requestDto.getStock() == null || requestDto.getStock() <= 0) {
            throw new IllegalArgumentException("Stock must be greater than 0");
        }

        Product product = Product.builder()
                .name(requestDto.getName())
                .price(requestDto.getPrice())
                .description(requestDto.getDescription())
                .stock(requestDto.getStock())
                .productStatus(ProductStatus.ON_SALE)
                .productCondition(requestDto.getProductCondition())
                .productMaker(requestDto.getProductMaker())
                .ownerUuid(member.getMemberUuid())
                .build();

        log.info("created product: {}", product);

        ProductResponseDto savedProduct = productRepository.save(product).toResponseDto();

        int currentAddImageCount = productImageRepository.countByProduct(product);
        int newImageCount = requestDto.getProductImages().size();

        if (newImageCount + currentAddImageCount > 8) {
            throw new IllegalArgumentException("Picture can be registered from 1 to 8");
        }
        List<ProductImage> images = requestDto.getProductImages().stream()
                .map(imagesDto -> ProductImage.builder()
                        .product(product)
                        .imageUrl(imagesDto.getImageUrl())
                        .ownerUuid(product.getOwnerUuid())
                        .build())
                .collect(Collectors.toList());

        productImageRepository.saveAll(images);

        return savedProduct;
    }

    @Override
    public Page<ProductResponseDto> searchProducts(String ownerUuid, String keyword, BigDecimal minPrice, BigDecimal maxPrice, String sortField, Pageable pageable) {

        log.info("Searching product for owner UUID:{}", ownerUuid);

        return productRepository.searchProduct(ownerUuid, keyword, minPrice, maxPrice, sortField, pageable);
    }


    @Override
    public ProductResponseDto getProduct(String ownerUuid, String productUuid) {
        Product product = productRepository.findByOwnerUuidAndProductUuid(ownerUuid, productUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found owner UUID or product UUID"));

        if (!Objects.equals(product.getOwnerUuid(), ownerUuid)) {
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

        log.info("updated product :{}", product);

        return product.toResponseDto();
    }


    @Override
    @Transactional
    public void deleteProduct(String ownerUuid, String productUuid) {

        log.info("Deleting product for member UUID: {}, product UUID: {}", ownerUuid, productUuid);

        Product product = productRepository.findByOwnerUuidAndProductUuid(ownerUuid, productUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found Owner UUID or Product UUID"));

        matchOwnerUuidAndProductUuid(ownerUuid, productUuid, product);

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
