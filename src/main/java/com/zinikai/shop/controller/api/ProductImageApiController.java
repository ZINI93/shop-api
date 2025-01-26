package com.zinikai.shop.controller.api;

import com.zinikai.shop.domain.product.dto.*;
import com.zinikai.shop.domain.product.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/product/productImage")
@RequiredArgsConstructor
public class ProductImageApiController {

    private final ProductImageService productImageService;


    @PostMapping
    public ResponseEntity<ProductImageResponseDto> createProductImage(@RequestBody ProductImageRequestDto requestDto){
        ProductImageResponseDto productImage = productImageService.createProductImage(requestDto);
        URI location = URI.create("/api/product/productImage" + productImage.getId());
        return ResponseEntity.created(location).body(productImage);
    }

    @GetMapping("{productImageId}")
    public ResponseEntity<ProductImageResponseDto> findByImageId(@PathVariable Long productImageId){
        ProductImageResponseDto productImageById = productImageService.getProductImageById(productImageId);
        return ResponseEntity.ok(productImageById);
    }

    @PutMapping("{productImageId}")
    public ResponseEntity<ProductImageResponseDto> editProductImage(@PathVariable Long productImageId,
                                                                    @RequestBody ProductImageUpdateDto UpdateDto){
        ProductImageResponseDto productImageResponseDto = productImageService.updateProductImage(productImageId, UpdateDto);
        return ResponseEntity.ok(productImageResponseDto);
    }

    @DeleteMapping("{productImageId}")
    public ResponseEntity<ProductImageResponseDto> deleteProductImage(@PathVariable Long productImageId){
        productImageService.deleteProductImage(productImageId);

        return ResponseEntity.noContent().build();
    }

}
