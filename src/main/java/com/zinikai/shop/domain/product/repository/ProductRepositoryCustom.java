package com.zinikai.shop.domain.product.repository;

import com.querydsl.core.QueryFactory;
import com.zinikai.shop.domain.member.dto.MemberResponseDto;
import com.zinikai.shop.domain.product.dto.ProductRequestDto;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductRepositoryCustom {

    Page<ProductResponseDto> searchProduct(String ownerUuid,String keyword, BigDecimal minPrice, BigDecimal maxPrice, String sortField, Pageable pageable);
}
