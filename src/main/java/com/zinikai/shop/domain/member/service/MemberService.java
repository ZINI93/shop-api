package com.zinikai.shop.domain.member.service;

import com.zinikai.shop.domain.member.dto.MemberRequestDto;
import com.zinikai.shop.domain.member.dto.MemberResponseDto;
import com.zinikai.shop.domain.member.dto.MemberUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberService {

    MemberResponseDto createMember(MemberRequestDto MemberRequestDto);
    MemberResponseDto getMemberById(Long id);
    Page<MemberResponseDto> getNameAndPhoneNumber(String name, String phoneNumber, Pageable pageable);
    List<MemberResponseDto> getAllMembers();
    MemberResponseDto updateMember(Long memberId, MemberUpdateDto updateDto);

    void deleteMember(Long memberId);


}
