package com.zinikai.shop.controller.api;


import com.zinikai.shop.domain.member.dto.MemberResponseDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    //お支払い　管理　- POSTMEN TEST 完了
    @GetMapping("/payment-manege")
    public ResponseEntity <List<PaymentResponseDto>> getAllPayment(){
        List<PaymentResponseDto> allPayment = paymentService.getAllPayment();
        return ResponseEntity.ok(allPayment);
    }

     // - 전체회원 - 전화번호, 이름으로 조회   - POSTMEN TEST 完了
    @GetMapping("/memberSearch")
    public ResponseEntity<Page<MemberResponseDto>> findByNameAndPhoneNum(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phoneNumber,
            Pageable pageable
    ){
        Page<MemberResponseDto> nameAndPhoneNumber = memberService.getNameAndPhoneNumber(name, phoneNumber, pageable);
        return ResponseEntity.ok(nameAndPhoneNumber);
    }

    //회원 삭제 - POSTMEN TEST 完了
    @DeleteMapping("/member/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long memberId){
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }

    //商品登録　ー　POSTMEN TEST 完了
    @PostMapping("/product")
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto requestDto){
        ProductResponseDto product = productService.createProduct(requestDto);
        URI location = URI.create("/api/product" + product.getId());
        return ResponseEntity.created(location).body(product);
    }

    //アップデート　ー　POSTMEN TEST 完了
    @PutMapping("/product/{productId}")
    public ResponseEntity<ProductResponseDto> editProduct(
            @PathVariable Long productId,
            @RequestBody ProductUpdateDto updateDto){
        ProductResponseDto updatedProduct = productService.updateProduct(productId, updateDto);
        return ResponseEntity.ok(updatedProduct);
    }

    // product 削除　　ー　POSTMEN TEST 完了
    @DeleteMapping({"{productId}"})
    public ResponseEntity<ProductResponseDto> deleteProduct(@PathVariable Long productId){
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }




}
