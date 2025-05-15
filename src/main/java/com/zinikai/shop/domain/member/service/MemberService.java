package com.zinikai.shop.domain.member.service;

import com.zinikai.shop.domain.member.dto.MemberRequestDto;
import com.zinikai.shop.domain.member.dto.MemberResponseDto;
import com.zinikai.shop.domain.member.dto.MemberUpdateDto;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberService {

    Member createMember(MemberRequestDto requestDto);
    MemberResponseDto createMemberWithValidate(MemberRequestDto MemberRequestDto);
    MemberResponseDto getMemberUuid(String memberUuid);
    Page<MemberResponseDto> getNameAndPhoneNumber(String memberUuid, String name, String phoneNumber, Pageable pageable);
    MemberResponseDto updateMember(String memberUuid, MemberUpdateDto updateDto);
    void deleteMember(String uuid);


}
