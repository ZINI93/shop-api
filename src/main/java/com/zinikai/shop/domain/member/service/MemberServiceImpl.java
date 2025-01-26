package com.zinikai.shop.domain.member.service;

import ch.qos.logback.core.joran.conditional.ThenAction;
import com.zinikai.shop.domain.member.dto.MemberRequestDto;
import com.zinikai.shop.domain.member.dto.MemberResponseDto;
import com.zinikai.shop.domain.member.dto.MemberUpdateDto;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.entity.MemberRole;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 会員登録
     */

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

        return memberRepository.save(member).toResponseDto();
    }

    /**
     * 会員IDで探す
     */
    @Override
    public MemberResponseDto getMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("idが見つかりません"));
        return member.toResponseDto();

    }


    /**
     * 会員全部探す
     */
    @Override
    public List<MemberResponseDto> getAllMembers() {
        List<Member> members = memberRepository.findAll();

        if (members.isEmpty()){
            throw new IllegalArgumentException("メンバーが見つかりません");
        }
        return members.stream()
                .map(Member::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     *  会員名前や電話番号で探す
     */

    @Override
    public Page<MemberResponseDto> getNameAndPhoneNumber(String name, String phoneNumber, Pageable pageable) {
        validateInputs(name,phoneNumber);
        return memberRepository.findByNameAndPhoneNumber(name, phoneNumber, pageable);

    }

    //名前、電話番号 検証! 　
    private void validateInputs(String name, String phoneNumber) {
        if (name.isEmpty()){
            throw new IllegalArgumentException("名前が空です。正しい名前を入力してください。");
        }
        if (phoneNumber.isEmpty()){
            throw new IllegalArgumentException("電話番号が空です。正しい名前を入力してください。");

        }
    }


    /**
     * 会員アップデート
     */
    @Override
    public MemberResponseDto updateMember(Long memberId, MemberUpdateDto updateDto) {
        Member member= memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("メンバーが見つかりません"));


       member.updateInfo(
               passwordEncoder.encode(updateDto.getPassword()),
               updateDto.getName(),
               updateDto.getPhoneNumber(),
               updateDto.getAddress()
       );

        return memberRepository.save(member).toResponseDto();
    }

    /**
     *  会員削除
     */
    @Override
    public void deleteMember(Long memberId) {
        if (!memberRepository.existsById(memberId)){
            throw new EntityNotFoundException("メンバーが見つかりません");
        }
        memberRepository.deleteById(memberId);
    }
}
