package com.zinikai.shop.controller.api;

import com.zinikai.shop.domain.cart.dto.CartRequestDto;
import com.zinikai.shop.domain.cart.dto.CartResponseDto;
import com.zinikai.shop.domain.cart.dto.CartUpdateDto;
import com.zinikai.shop.domain.cart.service.CartService;
import com.zinikai.shop.domain.member.service.CustomUserDetails;
import com.zinikai.shop.domain.product.dto.ProductUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;


@RequestMapping("/api/carts")
@RequiredArgsConstructor
@RestController
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponseDto> createCart(@RequestBody CartRequestDto requestDto,
                                                      Authentication authentication) {
        CustomUserDetails customUserDetails = getPrincipal(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        CartResponseDto cart = cartService.createCart(memberUuid, requestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{cartUuid}")
                .buildAndExpand(cart.getCartUuid())
                .toUri();

        return ResponseEntity.created(location).body(cart);
    }

    @GetMapping
    public ResponseEntity<Page<CartResponseDto>> getCarts(Authentication authentication,
                                                          @PageableDefault(size = 10, page = 0) Pageable pageable) {

        CustomUserDetails customUserDetails = getPrincipal(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        Page<CartResponseDto> carts = cartService.getCarts(memberUuid, pageable);

        return ResponseEntity.ok(carts);
    }

    @PutMapping("{cartUuid}")
    public ResponseEntity<CartResponseDto> editCart(@PathVariable String cartUuid,
                                                    Authentication authentication,
                                                    @RequestBody CartUpdateDto updateDto) {

        CustomUserDetails customUserDetails = getPrincipal(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        CartResponseDto updateCart = cartService.updateCart(memberUuid, cartUuid, updateDto);

        return ResponseEntity.ok(updateCart);
    }

    @DeleteMapping("{cartUuid}")
    public ResponseEntity<Void> deleteCart(@PathVariable String cartUuid,
                                           Authentication authentication) {

        CustomUserDetails customUserDetails = getPrincipal(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        cartService.deleteCart(memberUuid, cartUuid);

        return ResponseEntity.noContent().build();
    }

    private static CustomUserDetails getPrincipal(Authentication authentication) {
        return (CustomUserDetails) authentication.getPrincipal();
    }
}
