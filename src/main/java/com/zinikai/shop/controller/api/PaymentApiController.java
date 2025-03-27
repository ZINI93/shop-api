package com.zinikai.shop.controller.api;

import com.zinikai.shop.domain.member.service.CustomUserDetails;
import com.zinikai.shop.domain.payment.dto.PaymentRequestDto;
import com.zinikai.shop.domain.payment.dto.PaymentResponseDto;
import com.zinikai.shop.domain.payment.dto.PaymentUpdateDto;
import com.zinikai.shop.domain.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentApiController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<Page<PaymentResponseDto>> getPayments(Authentication authentication,
                                                                @PageableDefault(size = 10,page = 0)Pageable pageable) {
        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        Page<PaymentResponseDto> payments = paymentService.getPayments(memberUuid, pageable);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("{paymentUuId}")
    public ResponseEntity<PaymentResponseDto> editPayment(@PathVariable String paymentUuId,
                                                          Authentication authentication){

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        PaymentResponseDto payment = paymentService.getPayment(memberUuid, paymentUuId);

        return ResponseEntity.ok(payment);
    }

    @PutMapping("{paymentUuId}")
    public ResponseEntity<PaymentResponseDto> editPayment(@PathVariable String paymentUuId,
                                                          Authentication authentication,
                                                          @Valid @RequestBody PaymentUpdateDto updateDto){
        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        PaymentResponseDto payment = paymentService.updatePayment(memberUuid, paymentUuId, updateDto);
        return ResponseEntity.ok(payment);
    }
    @PutMapping("{paymentUuId}/completed")
    public ResponseEntity<PaymentResponseDto> confirmPayment(@PathVariable String paymentUuId,
                                                          Authentication authentication) {

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        PaymentResponseDto payment = paymentService.confirmPayment(memberUuid, paymentUuId);

        return ResponseEntity.ok(payment);
    }

    @PutMapping("{paymentUuId}/cancel")
    public ResponseEntity<PaymentResponseDto> cancelPayment(@PathVariable String paymentUuId,
                                                          Authentication authentication) {

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        PaymentResponseDto payment = paymentService.cancelPayment(memberUuid, paymentUuId);

        return ResponseEntity.ok(payment);
    }

    @DeleteMapping("{paymentUuid}")
    public ResponseEntity<Void> deletePayment(@PathVariable String paymentUuid,
                                              Authentication authentication){

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        paymentService.deletePayment(memberUuid,paymentUuid);
       return ResponseEntity.noContent().build();
    }

    private static CustomUserDetails getCustomUserDetails(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails;
    }
}
