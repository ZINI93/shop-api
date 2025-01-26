package com.zinikai.shop.controller.api;

import com.zinikai.shop.domain.member.dto.MemberRequestDto;
import com.zinikai.shop.domain.member.dto.MemberResponseDto;
import com.zinikai.shop.domain.member.dto.MemberUpdateDto;
import com.zinikai.shop.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    //전체회원조회
    @GetMapping
    public ResponseEntity<List<MemberResponseDto>> allMembers(){
        List<MemberResponseDto> allMembers = memberService.getAllMembers();
        return ResponseEntity.ok(allMembers);
    }
    // id로 조회
    @GetMapping("{memberId}")
    public ResponseEntity<MemberResponseDto> findById(@PathVariable Long memberId){
        MemberResponseDto memberById = memberService.getMemberById(memberId);
        return ResponseEntity.ok(memberById);
    }
    // admin // - 전체회원 - 전화번호, 이름으로 조회
    @GetMapping("/admin/memberSearch")
    public ResponseEntity<Page<MemberResponseDto>> findByNameAndPhoneNum(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phoneNumber,
            Pageable pageable
    ){
        Page<MemberResponseDto> nameAndPhoneNumber = memberService.getNameAndPhoneNumber(name, phoneNumber, pageable);
        return ResponseEntity.ok(nameAndPhoneNumber);
    }

    //회원가입
    @PostMapping
    public ResponseEntity<MemberResponseDto> createMember(@RequestBody MemberRequestDto requestDto){
        MemberResponseDto member = memberService.createMember(requestDto);
        URI location = URI.create("/api/member" + member.getId());
        return ResponseEntity.created(location).body(member);
        // 회원가입은 201로 반환하는게 좋이서 위에 처럼 구현
    }

    //회원 수정
    @PutMapping("{memberId}")
    public ResponseEntity<MemberResponseDto> editMember(@PathVariable Long memberId,
                                                          @RequestBody MemberUpdateDto updateDto){
        MemberResponseDto updatedMember = memberService.updateMember(memberId, updateDto);
        return ResponseEntity.ok(updatedMember);
    }

    //삭제
    @DeleteMapping("{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long memberId){
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }


    //valid 체크, 집에서 insert db 데이터 넣기

}
