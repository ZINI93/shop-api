package com.zinikai.shop.controller.api;

import com.zinikai.shop.domain.cart.dto.CartRequestDto;
import com.zinikai.shop.domain.cart.dto.CartResponseDto;
import com.zinikai.shop.domain.cart.dto.CartUpdateDto;
import com.zinikai.shop.domain.cart.service.CartService;
import com.zinikai.shop.domain.product.dto.ProductUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


@RequestMapping("/api/cart")
@RequiredArgsConstructor
@RestController
public class CartController {

    private final CartService cartService;


    //カートに追加 - POSTMEN TEST 完了
    @PostMapping
    public ResponseEntity<CartResponseDto> createCart(@RequestBody CartRequestDto requestDto){
        CartResponseDto cart = cartService.createCart(requestDto);
        URI location = URI.create("/api/cart/" + cart.getId());
        return  ResponseEntity.created(location).body(cart);
    }

    //カートを照会 - POSTMEN TEST 完了
    @GetMapping("{cartId}")
    public ResponseEntity<CartResponseDto> findById(@PathVariable Long cartId){
        CartResponseDto cart = cartService.findById(cartId);
        return  ResponseEntity.ok(cart);
    }

    //メンバー別、カートの商品のリスト　ー- POSTMEN TEST 完了
    @GetMapping
    public ResponseEntity<List<CartResponseDto>> findAllByMember(){
        List<CartResponseDto> carts = cartService.getAllCart();
        return ResponseEntity.ok(carts);
    }


    //カートをアップデート ー- POSTMEN TEST 完了
    @PutMapping("{cartId}")
    public ResponseEntity<CartResponseDto> editCart(@PathVariable Long cartId,
                                                    @RequestBody CartUpdateDto updateDto){
        CartResponseDto updateCart = cartService.updateCart(cartId, updateDto);
        return  ResponseEntity.ok(updateCart);
    }

    //カートを削除 ー- POSTMEN TEST 完了
    @DeleteMapping("{cartId}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long cartId){
        cartService.deleteCart(cartId);
        return ResponseEntity.noContent().build();
    }
}
