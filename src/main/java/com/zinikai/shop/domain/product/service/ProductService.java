package com.zinikai.shop.domain.product.service;

import com.zinikai.shop.domain.product.dto.ProductRequestDto;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import com.zinikai.shop.domain.product.dto.ProductUpdateDto;
import com.zinikai.shop.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    ProductResponseDto createProduct(ProductRequestDto productRequestDto);

    ProductResponseDto getProductById(Long productId);

    Page<ProductResponseDto>  searchProducts(String keyword, BigDecimal minPrice, BigDecimal maxPrice, String sortField, Pageable pageable);

    ProductResponseDto updateProduct(Long productId, ProductUpdateDto updateDto);

    void deleteProduct(Long productId);
}
