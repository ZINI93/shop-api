package com.zinikai.shop.domain.member.service;

import com.zinikai.shop.domain.mail.service.MailService;
import com.zinikai.shop.domain.member.dto.MemberRequestDto;
import com.zinikai.shop.domain.member.dto.MemberResponseDto;
import com.zinikai.shop.domain.member.dto.MemberUpdateDto;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.entity.MemberRole;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock private MemberRepository memberRepository;

    @Mock private MailService mailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberServiceImpl memberService;

    MemberRequestDto requestDto;

    Member member;

    private void setMemberId(Member member, Long id) throws Exception {
        Field field = member.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(member, id);
    }

    @BeforeEach
    void setup() throws Exception {
        //given
        requestDto = new MemberRequestDto(
                "test@gmail.com",
                "password",
                "zini",
                "080-1234-1234",
                MemberRole.USER
        );

        member = new Member(
                requestDto.getEmail(),
                requestDto.getPassword(),
                requestDto.getName(),
                requestDto.getPhoneNumber(),
                requestDto.getRole(),
                UUID.randomUUID().toString()
        );

        setMemberId(member, 1L);

    }

    /**
     * 회원가입 test
     */

    @Test
    @DisplayName("会員登録")
    void testCreateMember() {

        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("password");
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        //when
        mailService.sendWelcomeEmail(member.getEmail(),member.getName());
        MemberResponseDto savedMember = memberService.createMemberWithValidate(requestDto);

        //then
        assertNotNull(savedMember);
        assertEquals("test@gmail.com", savedMember.getEmail());
        assertEquals("zini", savedMember.getName());

        verify(passwordEncoder, times(1)).encode("password");
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    //회원 UUID 단건조회
    @Test
    @DisplayName("会員idで探す")
    void testFindByMemberId() {


        when(memberRepository.findByMemberUuid(member.getMemberUuid())).thenReturn(Optional.of(member));

        //when
        MemberResponseDto responseDto = memberService.getMemberUuid(member.getMemberUuid());

        //then
        assertNotNull(responseDto);
        assertEquals(member.getName(), responseDto.getName());
        verify(memberRepository, times(1)).findByMemberUuid(member.getMemberUuid());
    }


    // 회원 전화번호, 폰넘버 조회
    @Test
    @DisplayName("nameとphoneNumberで探す")
    void findByNameAndPhoneNumber() {
        //given


        PageRequest pageable = PageRequest.of(0, 10);
        List<MemberResponseDto> mockData = List.of(member.toResponseDto());
        PageImpl<MemberResponseDto> mockPage = new PageImpl<>(mockData, pageable, mockData.size());


        when(memberRepository.findByMemberUuid(member.getMemberUuid())).thenReturn(Optional.ofNullable(member));
        when(memberRepository.findByNameAndPhoneNumber(member.getName(), member.getPhoneNumber(), pageable)).thenReturn(mockPage);

        // when
        Page<MemberResponseDto> result = memberService.getNameAndPhoneNumber(member.getMemberUuid(),member.getName(), member.getPhoneNumber(), pageable);


        //then
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("test@gmail.com", result.getContent().get(0).getEmail());
        verify(memberRepository, times(1)).findByNameAndPhoneNumber(member.getName(), member.getPhoneNumber(), pageable);
    }

    // 회원수정

    @Test
    @DisplayName("会員アップデート")
    void updateMember() {
        //given
        MemberUpdateDto memberUpdateDto = new MemberUpdateDto(
                "pasword1234",
                "yuna",
                "080-1111-1111"
        );

        when(memberRepository.findByMemberUuid(member.getMemberUuid())).thenReturn(Optional.of(member));

        //when


        MemberResponseDto updateMember = memberService.updateMember(member.getMemberUuid(), memberUpdateDto);

        //then
        assertNotNull(updateMember);
        assertEquals(memberUpdateDto.getName(), updateMember.getName());
        assertEquals(memberUpdateDto.getPhoneNumber(), updateMember.getPhoneNumber());
    }


    //회원삭제
    @Test
    @DisplayName("メンバーを削除")
    void deleteMember() {
        //given
        when(memberRepository.findByMemberUuid(member.getMemberUuid())).thenReturn(Optional.ofNullable(member));

        //when
        memberService.deleteMember(member.getMemberUuid());

        //then
        verify(memberRepository, times(1)).delete(member);
    }

}