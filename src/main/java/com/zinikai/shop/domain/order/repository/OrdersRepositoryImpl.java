package com.zinikai.shop.domain.order.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zinikai.shop.domain.member.entity.QMember;
import com.zinikai.shop.domain.order.dto.OrdersResponseDto;
import com.zinikai.shop.domain.order.dto.QOrdersResponseDto;
import com.zinikai.shop.domain.order.entity.QOrders;
import com.zinikai.shop.domain.order.entity.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static com.zinikai.shop.domain.order.entity.QOrders.*;

@Repository
@RequiredArgsConstructor
public class OrdersRepositoryImpl implements OrdersRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<OrdersResponseDto> searchOrders(String memberUuid, Status status, LocalDateTime startDate, LocalDateTime endDate, BigDecimal minAmount, BigDecimal maxAmount, String sortField, Pageable pageable) {
        // 조건식 생성
        BooleanExpression predicate = buildPredicate(memberUuid, status, startDate, endDate, minAmount, maxAmount);

        //정렬 처리
        OrderSpecifier<?> sortOrder = getSortOrder(sortField);

        //데이터 조회
        List<OrdersResponseDto> orders = queryFactory
                .select(new QOrdersResponseDto(
                        QOrders.orders.id,
                        QMember.member.id,
                        QOrders.orders.totalAmount,
                        QOrders.orders.status,
                        QOrders.orders.paymentMethod,
                        QOrders.orders.sellerUuid
                ))
                .from(QOrders.orders)
                .leftJoin(QOrders.orders.member, QMember.member)  // 購入の情報を照会をするために、left joinを使いました。
                .where(predicate)
                .orderBy(sortOrder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //전체 데이터 개수
        Long total = Optional.ofNullable(queryFactory
                .select(QOrders.orders.count())
                .from(QOrders.orders)
                .fetchOne()).orElseThrow();

        return new PageImpl<>(orders, pageable, total);

    }

    private BooleanExpression buildPredicate(String memberUuid, Status status, LocalDateTime startDate, LocalDateTime endDate, BigDecimal minAmount, BigDecimal maxAmount) {
        return Stream.of(filterByMember(memberUuid)
                        .and(filterByStatus(status))
                        .and(filterByDateRange(startDate, endDate))
                        .and(filterByAmountRange(minAmount, maxAmount)
                        )).filter(Objects::nonNull)
                .reduce(Expressions.TRUE, BooleanExpression::and);
    }

    private BooleanExpression filterByMember(String memberUuid) {
        if (memberUuid == null || memberUuid.isEmpty()){
            return null;
        }
        return orders.member.memberUuid.eq(memberUuid);
    }

    private BooleanExpression filterByStatus(Status status) {
        if (status == null) {
            return null;
        }
        return orders.status.eq(status);
    }

    private BooleanExpression filterByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null && endDate == null) {
            return Expressions.TRUE;   // querydslはANDを使う時, NULLをそのまま、使ったらNullPointExceptionの　可能性がありま。
        } else if (startDate == null) {
            return orders.createdAt.isNotNull().and(orders.createdAt.loe(endDate));
        } else if (endDate == null) {
            return orders.createdAt.isNotNull().and(orders.createdAt.goe(startDate));
        } else {
            return orders.createdAt.isNotNull().and(orders.createdAt.between(startDate, endDate));
        }
    }

    private BooleanExpression filterByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        if (minAmount == null && maxAmount == null) {
            return Expressions.TRUE;
        } else if (minAmount == null) {
            return orders.totalAmount.isNotNull().and(orders.totalAmount.loe(maxAmount));
        } else if (maxAmount == null) {
            return orders.totalAmount.isNotNull().and(orders.totalAmount.goe(minAmount));
        } else if (minAmount.compareTo(maxAmount) == 0) {
            return orders.totalAmount.isNotNull().and(orders.totalAmount.goe(minAmount).and(orders.totalAmount.loe(maxAmount)));
        }
        return orders.totalAmount.isNotNull().and(orders.totalAmount.between(minAmount, maxAmount));
    }

//    　上のコードを改善コード
    private static final Map<String, OrderSpecifier<?>> SORT_FIELDS = Map.of(
            "created_at", orders.createdAt.desc(),
            "total_amount", orders.totalAmount.desc()
    );

    private OrderSpecifier<?> getSortOrder(String sortField) {
        return SORT_FIELDS.getOrDefault(sortField.toLowerCase(), orders.createdAt.desc());
    }
}
