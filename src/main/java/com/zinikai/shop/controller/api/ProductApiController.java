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
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductApiController {

    private final ProductService productService;


    //商品登録
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto requestDto){
        ProductResponseDto product = productService.createProduct(requestDto);
        URI location = URI.create("/api/product" + product.getId());
        return ResponseEntity.created(location).body(product);
    }

    // idで探す
    @GetMapping("{productId}")
    public ResponseEntity<ProductResponseDto> findById(@PathVariable Long productId){
        ProductResponseDto productById = productService.getProductById(productId);
        return ResponseEntity.ok(productById);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sortField,
            Pageable pageable){
        Page<ProductResponseDto> searchProducts = productService.searchProducts(keyword, minPrice,maxPrice,sortField,pageable);
        return ResponseEntity.ok(searchProducts);
    }

    //アップデート
    @PutMapping("{productId}")
    public ResponseEntity<ProductResponseDto> editProduct(
            @PathVariable Long productId,
            @RequestBody ProductUpdateDto updateDto){
        ProductResponseDto updatedProduct = productService.updateProduct(productId, updateDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping({"{productId}"})
    public ResponseEntity<ProductResponseDto> deleteProduct(@PathVariable Long productId){
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
