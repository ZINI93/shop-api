package com.zinikai.shop.controller.api;

import com.zinikai.shop.domain.member.service.CustomUserDetails;
import com.zinikai.shop.domain.product.dto.ProductRequestDto;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import com.zinikai.shop.domain.product.dto.ProductUpdateDto;
import com.zinikai.shop.domain.product.repository.ProductRepository;
import com.zinikai.shop.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductApiController {

    private final ProductService productService;


    //商品登録　ー　POSTMEN TEST 完了
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto requestDto,
                                                            Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = customUserDetails.getMemberId();

        ProductResponseDto product = productService.createProduct(memberId, requestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(product.getId())
                .toUri();

        return ResponseEntity.created(location).body(product);
    }

    /**
     * 商品サーチ
     */

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sortField,
            @PageableDefault(size = 20, page = 0) Pageable pageable,
            Authentication authentication) {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String memberUuid = customUserDetails.getMemberUuid();

        Page<ProductResponseDto> searchProducts = productService.searchProducts(memberUuid, keyword, minPrice, maxPrice, sortField, pageable);
        return ResponseEntity.ok(searchProducts);
    }

    @PutMapping("{productUuid}")
    public ResponseEntity<ProductResponseDto> updateProduct(@Valid @PathVariable String productUuid,
                                                            @RequestBody ProductUpdateDto updateDto,
                                                            Authentication authentication) {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String memberUuid = customUserDetails.getMemberUuid();

        ProductResponseDto updatedProduct = productService.updateProduct(memberUuid, productUuid, updateDto);

        return ResponseEntity.ok(updatedProduct);

    }

    @DeleteMapping("{productUuid}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productUuid,
                                              Authentication authentication) {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String memberUuid = customUserDetails.getMemberUuid();

        productService.deleteProduct(memberUuid, productUuid);

        return ResponseEntity.noContent().build();

    }


}
