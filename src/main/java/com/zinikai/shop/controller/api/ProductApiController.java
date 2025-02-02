package com.zinikai.shop.controller.api;

import com.zinikai.shop.domain.product.dto.ProductRequestDto;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import com.zinikai.shop.domain.product.dto.ProductUpdateDto;
import com.zinikai.shop.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductApiController {

    private final ProductService productService;


    // idで探す - POSTMEN TEST 完了
    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductResponseDto> findById(@PathVariable Long productId){
        ProductResponseDto productById = productService.getProductById(productId);
        return ResponseEntity.ok(productById);
    }

    // ー　POSTMEN TEST 完了

    @GetMapping("/products")
    public ResponseEntity<Page<ProductResponseDto>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sortField,
            Pageable pageable){
        Page<ProductResponseDto> searchProducts = productService.searchProducts(keyword, minPrice,maxPrice,sortField,pageable);
        return ResponseEntity.ok(searchProducts);
    }

}
