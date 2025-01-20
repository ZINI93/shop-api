package com.zinikai.shop.domain.member.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zinikai.shop.domain.member.dto.MemberResponseDto;
import com.zinikai.shop.domain.member.dto.QMemberResponseDto;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.entity.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.zinikai.shop.domain.member.entity.QMember.*;


@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

private final JPAQueryFactory queryFactory;


    @Override
    public Page<MemberResponseDto> findByNameAndPhoneNumber(String name, String phoneNumber, Pageable pageable) {

        List<MemberResponseDto> content = queryFactory
                .select(new QMemberResponseDto(
                        member.id.as("memberId"),
                        member.email,
                        member.password,
                        member.phoneNumber,
                        member.address))
                .from(member)
                .where(phoneNumberEq(phoneNumber),
                        nameEq(name))
                .orderBy(member.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(member)
                .where(phoneNumberEq(phoneNumber),
                        nameEq(name))
                .orderBy(member.id.desc())
                .fetch()   // 最新, fetchcountは、使わない
                .size();

        return new PageImpl<>(content,pageable,total);
    }

    private BooleanExpression phoneNumberEq(String phoneNumberCnd) {
        return isNotEmpty(phoneNumberCnd) ? member.phoneNumber.eq(phoneNumberCnd) : null;
    }

    private BooleanExpression nameEq(String nameCnd) {
        return isNotEmpty(nameCnd) ? member.name.eq(nameCnd) : null;
    }

    private boolean isNotEmpty(String value){
        return value!=null&& !value.isEmpty();
    }  // 검증로직
}
