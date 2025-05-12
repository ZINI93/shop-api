package com.zinikai.shop.domain.product.service;

import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.product.dto.ProductRequestDto;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import com.zinikai.shop.domain.product.dto.ProductUpdateDto;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.entity.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {


    ProductResponseDto createProductProcess(String memberUuid, ProductRequestDto requestDto);

    Page<ProductResponseDto>  searchProducts(String ownerUuid,String keyword, BigDecimal minPrice, BigDecimal maxPrice, String sortField, Pageable pageable);

    ProductResponseDto getProduct(String productUuid);

    ProductResponseDto updateProduct(String ownerUuid, String productUuid, ProductUpdateDto updateDto);

    void deleteProduct(String ownerUuid,String productUuid);

    void validateProduct(List<Product> products, String sellerUuid);
}
