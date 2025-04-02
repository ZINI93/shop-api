package com.zinikai.shop.domain.member.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zinikai.shop.domain.member.dto.MemberResponseDto;
import com.zinikai.shop.domain.member.dto.QMemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.zinikai.shop.domain.member.entity.QMember.*;


@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MemberResponseDto> findByNameAndPhoneNumber(String name, String phoneNumber, Pageable pageable) {

        List<MemberResponseDto> content = queryFactory
                .select(new QMemberResponseDto(
                        member.email,
                        member.name,
                        member.phoneNumber))
                .from(member)
                .where(phoneNumberCond(phoneNumber),
                        nameCond(name))
                .orderBy(member.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        // 배웠던 fetchCount 를 대신 사용
        Long total = Optional.ofNullable(queryFactory
                .select(member.id.count())
                .from(member)
                .fetchOne()).orElseThrow();  // fetchOne 을 쓸때 null check

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression phoneNumberCond(String phoneNumberCnd) {
        return containCond(phoneNumberCnd, member.phoneNumber);

    }

    private BooleanExpression nameCond(String nameCnd) {
        return containCond(nameCnd, member.name);

    }

    private static BooleanExpression containCond(String value, StringPath field) {
        if (value == null || value.isEmpty()){
            return null;
        }
        return field.containsIgnoreCase(value);
    }
}
