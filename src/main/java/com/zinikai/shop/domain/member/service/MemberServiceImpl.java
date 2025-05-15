package com.zinikai.shop.domain.member.service;

import com.zinikai.shop.domain.mail.service.MailService;
import com.zinikai.shop.domain.member.dto.MemberRequestDto;
import com.zinikai.shop.domain.member.dto.MemberResponseDto;
import com.zinikai.shop.domain.member.dto.MemberUpdateDto;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.entity.MemberRole;
import com.zinikai.shop.domain.member.exception.GradeMissMatchException;
import com.zinikai.shop.domain.member.exception.MemberNotFoundException;
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
    private final MailService mailService;

    @Override
    public Member createMember(MemberRequestDto requestDto) {

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        return Member.builder()
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .name(requestDto.getName())
                .phoneNumber(requestDto.getPhoneNumber())
                .build();
    }

    @Override
    @Transactional
    public MemberResponseDto createMemberWithValidate(MemberRequestDto requestDto) {

        Member member = createMember(requestDto);
        Member savedMember = memberRepository.save(member);

        mailService.sendWelcomeEmail(member.getEmail(), member.getName());

        log.info("Created savedMember UUID:{}", member);

        return savedMember.toResponseDto();
    }


    @Override
    public MemberResponseDto getMemberUuid(String memberUuid) {

        log.info("Searching member for member UUID:{}", memberUuid);

        Member member = findMemberByMemberUuid(memberUuid);

        return member.toResponseDto();
    }


    @Override
    public Page<MemberResponseDto> getNameAndPhoneNumber(String memberUuid, String name, String phoneNumber, Pageable pageable) {

        Member member = findMemberByMemberUuid(memberUuid);
        validateMemberGrade(member);

        return memberRepository.findByNameAndPhoneNumber(name, phoneNumber, pageable);
    }

    @Override
    @Transactional
    public MemberResponseDto updateMember(String memberUuid, MemberUpdateDto updateDto) {

        log.info("Updating member for member UUID:{}", memberUuid);

        Member member = findMemberByMemberUuid(memberUuid);

        String encodedPassword = updateDto.getPassword() != null ?
                passwordEncoder.encode(updateDto.getPassword()) :
                member.getPassword();

        member.updateInfo(
                encodedPassword,
                updateDto.getName(),
                updateDto.getPhoneNumber()
        );

        log.info("Updated member UUID:{}", member.getMemberUuid());

        return member.toResponseDto();
    }

    @Override
    @Transactional
    public void deleteMember(String memberUuid) {

        log.info("Deleting member for member UUID:{} ", memberUuid);

        Member member = findMemberByMemberUuid(memberUuid);

        memberRepository.delete(member);
    }

    private Member findMemberByMemberUuid(String memberUuid) {
        return memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new MemberNotFoundException("Member UUID: Member not found"));
    }

    private void validateMemberGrade(Member member) {
        if (member.getRole() != MemberRole.ADMIN) {
            throw new GradeMissMatchException("Insufficient privileges. Admin grade required ");
        }
    }
}