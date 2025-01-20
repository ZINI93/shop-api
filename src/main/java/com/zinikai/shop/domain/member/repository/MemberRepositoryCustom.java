package com.zinikai.shop.domain.member.repository;

import com.zinikai.shop.domain.member.dto.MemberResponseDto;
import com.zinikai.shop.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {
    Page<MemberResponseDto> findByNameAndPhoneNumber(String name, String phoneNumber, Pageable pageable);
}
