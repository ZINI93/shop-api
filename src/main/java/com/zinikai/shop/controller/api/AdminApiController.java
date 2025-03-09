package com.zinikai.shop.controller.api;


import com.zinikai.shop.domain.member.dto.MemberResponseDto;
import com.zinikai.shop.domain.member.service.CustomUserDetails;
import com.zinikai.shop.domain.member.service.MemberService;
import com.zinikai.shop.domain.payment.dto.PaymentResponseDto;
import com.zinikai.shop.domain.payment.service.PaymentService;
import com.zinikai.shop.domain.product.dto.ProductRequestDto;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import com.zinikai.shop.domain.product.dto.ProductUpdateDto;
import com.zinikai.shop.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminApiController {

    private final PaymentService paymentService;
    private final ProductService productService;
    private final MemberService memberService;


    @GetMapping("/members/search")
    public ResponseEntity<Page<MemberResponseDto>> findByNameAndPhoneNum(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phoneNumber,
            @PageableDefault(size = 20, page = 0) Pageable pageable
    ){
        Page<MemberResponseDto> nameAndPhoneNumber = memberService.getNameAndPhoneNumber(name, phoneNumber, pageable);
        return ResponseEntity.ok(nameAndPhoneNumber);
    }

    @DeleteMapping("/member/{memberUuId}")
    public ResponseEntity<Void> deleteMember(@PathVariable String memberUuid){
        memberService.deleteMember(memberUuid);
        return ResponseEntity.noContent().build();
    }

    //회원 거래내역, 회원 주문내역, 거래 전체 내역 구현필요함

}
