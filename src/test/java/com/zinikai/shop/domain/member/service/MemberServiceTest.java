package com.zinikai.shop.domain.member.service;

import com.zinikai.shop.domain.member.dto.MemberRequestDto;
import com.zinikai.shop.domain.member.dto.MemberResponseDto;
import com.zinikai.shop.domain.member.dto.MemberUpdateDto;
import com.zinikai.shop.domain.member.entity.Address;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock private MemberRepository memberRepository;

    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private MemberServiceImpl memberService;


    @BeforeEach
    void setup(){}

    /**
     * 회원가입 test
     */

    @Test
    @DisplayName("会員登録")
    void testCreateMember() {
        //given
        MemberRequestDto requestDto = new MemberRequestDto(
                "test@gmail.com",
                "password",
                "zini",
                "080-1234-1234",
                new Address("kanagawa", "miyamae", "123-123"),
                MemberRole.USER
        );
        Member tsetMember = new Member(
                1L,
                "test@gmail.com",
                "password",
                "zini",
                "080-1234-1234",
                new Address("kanagawa", "miyamae", "123-123"),
                MemberRole.USER
        );
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("password");
        when(memberRepository.save(any(Member.class))).thenReturn(tsetMember);

        //when
        MemberResponseDto savedMember = memberService.createMember(requestDto);

        //then
        assertNotNull(savedMember);
        assertEquals("test@gmail.com", savedMember.getEmail());
        assertEquals("zini", savedMember.getName());

        verify(passwordEncoder, times(1)).encode("password");
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    //회원 ID 단건조회
    @Test
    @DisplayName("会員idで探す")
    void testFindByMemberId(){

        //given
        MemberRequestDto requestDto = new MemberRequestDto(
                "test@gmail.com",
                "password",
                "zini",
                "080-1234-1234",
                new Address("kanagawa", "miyamae", "123-123"),
                MemberRole.USER
        );
        Member tsetMember = new Member(
                1L,
                "test@gmail.com",
                "password",
                "zini",
                "080-1234-1234",
                new Address("kanagawa", "miyamae", "123-123"),
                MemberRole.USER
        );


        when(memberRepository.findById(1L)).thenReturn(Optional.ofNullable(tsetMember));

        //when
        MemberResponseDto responseDto = memberService.getMemberById(1L);

        //then
        assertNotNull(responseDto);
        assertEquals(tsetMember.getName(),responseDto.getName());
        verify(memberRepository,times(1)).findById(1L);
    }

    // 전체 회원 조회
    @Test
    @DisplayName("会員全部探す")
    void testAllMembers(){
        //given
        Member tsetMember = new Member(
                1L,
                "test@gmail.com",
                "password",
                "zini",
                "080-1234-1234",
                new Address("kanagawa", "miyamae", "123-123"),
                MemberRole.USER
        );

        List<Member> memberlist = Collections.singletonList(tsetMember);
        when(memberRepository.findAll()).thenReturn(memberlist);

        //When
        List<MemberResponseDto> members = memberService.getAllMembers();

        //then
        assertNotNull(members);
        assertEquals(1, members.size());
        verify(memberRepository, times(1)).findAll();

    }
    // 회원 전화번호, 폰넘버 조회
    @Test
    @DisplayName("nameとphoneNumberで探す")
    void findByNameAndPhoneNumber(){
        //given
        PageRequest pageable = PageRequest.of(0, 5);

        List<MemberResponseDto> mockData = List.of(
                new MemberResponseDto(1L, "test1@gmail.com", "zini", "080-1234-5678", new Address("Tokyo", "Shinjuku", "123-456"), MemberRole.USER),
                new MemberResponseDto(2L, "test2@gmail.com", "zini", "080-9876-5432", new Address("Osaka", "Namba", "456-789"), MemberRole.USER)
        );
        PageImpl<MemberResponseDto> mockPage = new PageImpl<>(mockData, pageable, 2);

        when(memberRepository.findByNameAndPhoneNumber("zini", "080-1234-5678", pageable)).thenReturn(mockPage);

        // when
        Page<MemberResponseDto> result = memberService.getNameAndPhoneNumber("zini", "080-1234-5678", pageable);
        //then
        assertEquals(2,result.getTotalElements());
        assertEquals(2,result.getContent().size());
        assertEquals("test1@gmail.com", result.getContent().get(0).getEmail());
        verify(memberRepository, times(1)).findByNameAndPhoneNumber("John", "080-1234-5678", pageable);
    }

    // 회원수정

    @Test
    @DisplayName("会員アップデート")
    void updateMember(){
        //given
        Member tsetMember = new Member(
                1L,
                "test@gmail.com",
                "password",
                "zini",
                "080-1234-1234",
                new Address("kanagawa", "miyamae", "123-123"),
                MemberRole.USER
        );

        MemberUpdateDto memberUpdateDto = new MemberUpdateDto(
                "pasword1234",
                "yuna",
                "080-1111-1111",
                new Address("saitama", "kakaku", "123-153")
        );


        //when
        when(memberRepository.findById(1L)).thenReturn(Optional.of(tsetMember));
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MemberResponseDto updateMember = memberService.updateMember(1L, memberUpdateDto);

        //then
        assertNotNull(updateMember);
        assertEquals(memberUpdateDto.getName(), updateMember.getName());
        assertEquals(memberUpdateDto.getPhoneNumber(), updateMember.getPhoneNumber());
        assertEquals(memberUpdateDto.getAddress(), updateMember.getAddress());
        verify(memberRepository, times(1)).save(any(Member.class));
    }


    //회원삭제
    @Test
    @DisplayName("メンバーを削除")
    void deleteMember(){
        //given
        when(memberRepository.existsById(1L)).thenReturn(true);
        doNothing().when(memberRepository).deleteById(1L);

        //when
        memberService.deleteMember(1L);

        //then
        verify(memberRepository,times(1)).deleteById(1L);
    }

}