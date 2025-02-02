package com.zinikai.shop.controller.api;

import com.zinikai.shop.domain.member.dto.MemberRequestDto;
import com.zinikai.shop.domain.member.dto.MemberResponseDto;
import com.zinikai.shop.domain.member.dto.MemberUpdateDto;
import com.zinikai.shop.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    // id로 조회 POSTMEN TEST 完了
    @GetMapping("{memberId}")
    public ResponseEntity<MemberResponseDto> findById(@PathVariable Long memberId){
        MemberResponseDto memberById = memberService.getMemberById(memberId);
        return ResponseEntity.ok(memberById);
    }

    //회원가입 - POSTMEN TEST 完了
    @PostMapping("/join")
    public ResponseEntity<MemberResponseDto> createMember(@RequestBody MemberRequestDto requestDto){
        MemberResponseDto member = memberService.createMember(requestDto);
        URI location = URI.create("/api/member" + member.getId());
        return ResponseEntity.created(location).body(member);
        // 회원가입은 201로 반환하는게 좋이서 위에 처럼 구현
    }

    //회원 수정  - POSTMEN TEST 完了
    @PutMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> editMember(@PathVariable Long memberId,
                                                          @RequestBody MemberUpdateDto updateDto){
        MemberResponseDto updatedMember = memberService.updateMember(memberId, updateDto);
        return ResponseEntity.ok(updatedMember);
    }
}
