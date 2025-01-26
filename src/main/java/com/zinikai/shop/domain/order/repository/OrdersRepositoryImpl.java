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
import java.util.List;
import java.util.Map;

import static com.zinikai.shop.domain.order.entity.QOrders.*;

@Repository
@RequiredArgsConstructor
public class OrdersRepositoryImpl implements OrdersRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<OrdersResponseDto> searchOrders(Status status, LocalDateTime startDate, LocalDateTime endDate, BigDecimal minAmount, BigDecimal maxAmount, String sortField, Pageable pageable) {
        // 조건식 생성
        BooleanExpression predicate = buildPredicate(status, startDate, endDate, minAmount, maxAmount);


        //정렬 처리
        OrderSpecifier<?> sortOrder = getSortOrder(sortField);

        //데이터 조회
        List<OrdersResponseDto> orders = queryFactory
                .select(new QOrdersResponseDto(
                        QOrders.orders.id,
                        QMember.member.id,
                        QOrders.orders.totalAmount,
                        QOrders.orders.status,
                        QOrders.orders.paymentMethod
                ))
                .from(QOrders.orders)
                .leftJoin(QOrders.orders.member, QMember.member)  // 購入の情報を照会をするために、left joinを使いました。
                .where(predicate)
                .orderBy(sortOrder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //전체 데이터 개수
        Long total = queryFactory
                .select(QOrders.orders.count())
                .from(QOrders.orders)
                .where(predicate)
                .fetchOne();

        return new PageImpl<>(orders,pageable,total);


    }

    private BooleanExpression buildPredicate(Status status, LocalDateTime startDate, LocalDateTime endDate, BigDecimal minAmount, BigDecimal maxAmount) {
        return filterByStatus(status)
                .and(filterByDateRange(startDate, endDate))
                .and(filterByAmountRange(minAmount, maxAmount));
    }

    private BooleanExpression filterByStatus(Status status) {
        return status != null ? orders.status.eq(status) : null;
    }

    private BooleanExpression filterByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null && endDate == null){
            return Expressions.TRUE;   // querydslはANDを使う時, NULLをそのまま、使ったらNullPointExceptionの　可能性がありま。
        } else if (startDate == null){
            return orders.createdAt.loe(endDate);
        } else if ( endDate == null){
            return orders.createdAt.goe(startDate);
        }else {
            return orders.createdAt.between(startDate,endDate);
        }
    }

    private BooleanExpression filterByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        if (minAmount == null && maxAmount == null){
            return Expressions.TRUE;
        } else if (minAmount == null){
            return orders.totalAmount.loe(maxAmount);
        } else if ( maxAmount == null){
            return orders.totalAmount.goe(minAmount);
        } return orders.totalAmount.between(minAmount, maxAmount);

    }
//    private OrderSpecifier<?> getSortOrder(String sortField) {
//        if ("createdAt".equalsIgnoreCase(sortField)) {
//            return orders.createdAt.desc(); //  주문 시간 역순 정렬
//        } else if ("totalAmount".equalsIgnoreCase(sortField)) {
//            return orders.totalAmount.desc(); // 총 금액 역순 정렬
//        }
//        return orders.id.desc(); //기본정렬 id 역순
//    }

    //　上のコードを改善コード
    private static final Map<String, OrderSpecifier<?>> SORT_FIELDS = Map.of(
            "createdAt", orders.createdAt.desc(),
            "totalAmount", orders.totalAmount.desc()
    );
    private OrderSpecifier<?> getSortOrder(String sortField) {
        return SORT_FIELDS.getOrDefault(sortField.toLowerCase(),orders.id.desc());
    }

//    getOrDefault()를 사용하여 기본 정렬 필드(id.desc()) 처리.
//            Map.of()를 활용하면 if-else 없이 바로 정렬 필드를 가져올 수 있음.
}
