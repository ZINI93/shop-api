package com.zinikai.shop.domain.member.service;

import com.zinikai.shop.domain.member.dto.MemberRequestDto;
import com.zinikai.shop.domain.member.dto.MemberResponseDto;
import com.zinikai.shop.domain.member.dto.MemberUpdateDto;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.entity.MemberRole;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public MemberResponseDto createMember(MemberRequestDto requestDto) {

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        Member member = Member.builder()
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .name(requestDto.getName())
                .phoneNumber(requestDto.getPhoneNumber())
                .address(requestDto.getAddress())
                .role(MemberRole.USER)
                .build();

        log.info("Created savedMember:{}", member);

        return memberRepository.save(member).toResponseDto();
    }


    @Override
    public MemberResponseDto getMemberUuid(String memberUuid) {

        log.info("Searching member for member UUID:{}", memberUuid);

        Member member = getUuid(memberUuid);

        return member.toResponseDto();
    }

    /**
     * 管理者のサーチ機能
     */

    @Override
    public Page<MemberResponseDto> getNameAndPhoneNumber(String name, String phoneNumber, Pageable pageable) {
        return memberRepository.findByNameAndPhoneNumber(name, phoneNumber, pageable);
    }

    @Override
    @Transactional
    public MemberResponseDto updateMember(String memberUuid, MemberUpdateDto updateDto) {

        log.info("Updating member for member UUID:{}", memberUuid);

        Member member = getUuid(memberUuid);

        String encodedPassword = updateDto.getPassword() != null ?
                passwordEncoder.encode(updateDto.getPassword()) :
                member.getPassword();

        member.updateInfo(
                encodedPassword,
                updateDto.getName(),
                updateDto.getPhoneNumber(),
                updateDto.getAddress()
        );

        log.info("updated member:{}", member);

        return member.toResponseDto();
    }

    @Override
    @Transactional
    public void deleteMember(String memberUuid) {

        log.info("Deleting member for member UUID:{} ", memberUuid);

        Member member = getUuid(memberUuid);

        memberRepository.delete(member);
    }

    private Member getUuid(String memberUuid) {
        return memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> {
                    log.error("Member not found for UUID: {}", memberUuid);
                    return new IllegalArgumentException("Not found member UUID");
                });
    }
}
