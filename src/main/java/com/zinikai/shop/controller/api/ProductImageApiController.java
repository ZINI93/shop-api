package com.zinikai.shop.controller.api;

import com.zinikai.shop.domain.member.service.CustomUserDetails;
import com.zinikai.shop.domain.product.dto.*;
import com.zinikai.shop.domain.product.service.ProductImageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-images")
@RequiredArgsConstructor
public class ProductImageApiController {

    private final ProductImageService productImageService;

    @GetMapping("{productUuid}")
    public ResponseEntity<List<ProductImageResponseDto>> getImagesInProduct(@PathVariable String productUuid) {

        List<ProductImageResponseDto> productImages = productImageService.getImagesInProduct(productUuid);

        return ResponseEntity.ok(productImages);

    }

    @GetMapping("/product/{productUuid}")
    public ResponseEntity<ProductWithImagesDto> getProductInfoWithImages(@PathVariable String productUuid) {

        ProductWithImagesDto productWithImages = productImageService.getProductWithImages(productUuid);

        return ResponseEntity.ok(productWithImages);

    }

    @GetMapping("/all-images")
    public ResponseEntity<Page<ProductImageResponseDto>> getMyImages(Authentication authentication,
                                                                  @PageableDefault(size = 10, page = 0) Pageable pageable) {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String ownerUuid = customUserDetails.getMemberUuid();

        Page<ProductImageResponseDto> productImages = productImageService.getImagesByMember(ownerUuid, pageable);

        return ResponseEntity.ok(productImages);
    }

    @PutMapping("{productImageUuid}")
    public ResponseEntity<ProductImageResponseDto> editProductImage(@PathVariable String productImageUuid,
                                                                 Authentication authentication,
                                                                 @Valid @RequestBody ProductImageUpdateDto UpdateDto) {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String memberUuid = customUserDetails.getMemberUuid();

        ProductImageResponseDto productImageResponseDto = productImageService.updateProductImage(memberUuid, productImageUuid, UpdateDto);
        return ResponseEntity.ok(productImageResponseDto);
    }

    @DeleteMapping("{productImageUuid}")
    public ResponseEntity<Void> deleteProductImage(@PathVariable String productImageUuid,
                                                   Authentication authentication) {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String memberUuid = customUserDetails.getMemberUuid();

        try {
            productImageService.deleteProductImage(memberUuid,productImageUuid);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            // 권한 없음
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            // 잘못된 요청
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
