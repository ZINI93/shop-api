package com.zinikai.shop.controller.api;

import com.zinikai.shop.domain.member.dto.MemberRequestDto;
import com.zinikai.shop.domain.member.dto.MemberResponseDto;
import com.zinikai.shop.domain.member.dto.MemberUpdateDto;
import com.zinikai.shop.domain.member.service.CustomUserDetails;
import com.zinikai.shop.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<MemberResponseDto> createMember(@Valid @RequestBody MemberRequestDto requestDto){
        MemberResponseDto member = memberService.createMemberWithValidate(requestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{memberUuid}")
                .buildAndExpand(member.getMemberUuid())
                .toUri();
        return ResponseEntity.created(location).body(member);
    }

    @GetMapping("/members/me")
    public ResponseEntity<MemberResponseDto> findById(Authentication authentication){
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String memberUuid = customUserDetails.getMemberUuid();

        MemberResponseDto memberById = memberService.getMemberUuid(memberUuid);
        return ResponseEntity.ok(memberById);
    }


    @PutMapping("/members/update")
    public ResponseEntity<MemberResponseDto> editMember(@Valid @RequestBody MemberUpdateDto updateDto,
                                                        Authentication authentication){

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String memberUuid = customUserDetails.getMemberUuid();

        MemberResponseDto updatedMember = memberService.updateMember(memberUuid, updateDto);
        return ResponseEntity.ok(updatedMember);
    }
}
