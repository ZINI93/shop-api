package com.zinikai.shop.domain.product.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import com.zinikai.shop.domain.product.dto.QProductResponseDto;
import com.zinikai.shop.domain.product.entity.QProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.zinikai.shop.domain.product.entity.QProduct.*;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductResponseDto> searchProduct(String ownerUuid, String keyword, BigDecimal minPrice, BigDecimal maxPrice, String sortField, Pageable pageable) {
        BooleanExpression predicate = priceRangeCond(minPrice, maxPrice)
                .and(productOwnerUuidContains(ownerUuid))
                .and(productNameContains(keyword));

        OrderSpecifier<?> sortOrder = getSortOrder(sortField);

        List<ProductResponseDto> products = queryFactory.
                select(new QProductResponseDto(
                        product.id.as("productId"),
                        product.name,
                        product.price,
                        product.description,
                        product.stock))
                .from(product)
                .where(predicate)
                .orderBy(sortOrder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // count 쿼리
        Long total = Optional.ofNullable(queryFactory
                .select(product.id.count()) // count만 계산
                .from(product)
                .where(predicate)
                .fetchOne()).orElseThrow();

        return new PageImpl<>(products, pageable , total);

    }

    private Predicate productOwnerUuidContains(String ownerUuid) {
        return product.ownerUuid.eq(ownerUuid);
    }

    //　価格範囲設定
    private BooleanExpression priceRangeCond(BigDecimal minPrice, BigDecimal maxPrice) {
        if (maxPrice == null && minPrice == null) {
            return Expressions.TRUE;
        } else if (minPrice == null) {
            return product.price.isNotNull().and(product.price.loe(maxPrice));
        } else if (maxPrice == null) {
            return product.price.isNotNull().and(product.price.goe(minPrice));
        } else {
            return product.price.isNotNull().and(product.price.between(minPrice, maxPrice));
        }
    }
    private BooleanExpression productNameContains(String keyword) {
        if (keyword == null){
            return Expressions.TRUE;
        }
        return product.name.containsIgnoreCase(keyword);
    }

    private OrderSpecifier<?> getSortOrder(String sortField){
        if ("price".equalsIgnoreCase(sortField)){
            return product.price.asc();
        }else {
            return product.id.asc();
        }
    }
}
