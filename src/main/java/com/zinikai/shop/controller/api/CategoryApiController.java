package com.zinikai.shop.controller.api;


import com.zinikai.shop.domain.category.dto.CategoryRequestDto;
import com.zinikai.shop.domain.category.dto.CategoryResponseDto;
import com.zinikai.shop.domain.category.dto.CategoryUpdateDto;
import com.zinikai.shop.domain.category.service.CategoryService;
import com.zinikai.shop.domain.member.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('ADMIN')")
public class CategoryApiController {


    private final CategoryService categoryService;

    @PostMapping
    ResponseEntity<CategoryResponseDto> createCategory(Authentication authentication,
                                                       @RequestBody CategoryRequestDto requestDto) {

        CustomUserDetails userDetails = getCustomUserDetails(authentication);
        String memberUuid = userDetails.getMemberUuid();


        CategoryResponseDto category = categoryService.createCategory(memberUuid, requestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{categoryUuid}")
                .buildAndExpand(category.getCategoryUuid())
                .toUri();


        return ResponseEntity.created(location).body(category);
    }


    @GetMapping("{categoryUuid}")
    ResponseEntity<CategoryResponseDto> getCategoryInfo(Authentication authentication,
                                                        @PathVariable String categoryUuid){

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        CategoryResponseDto category = categoryService.getCategoryInfo(memberUuid, categoryUuid);


        return ResponseEntity.ok(category);
    }

    @GetMapping("/list")
    ResponseEntity<Page<CategoryResponseDto>> getCategoryList(Authentication authentication,
                                                             @PageableDefault(size = 10, page = 0) Pageable pageable) {


        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();


        Page<CategoryResponseDto> categoryList = categoryService.getCategoryList(memberUuid, pageable);

        return ResponseEntity.ok(categoryList);

    }

    @GetMapping("/top")
    ResponseEntity<Page<CategoryResponseDto>> getParentCategoryList(@PageableDefault(size = 10, page = 0) Pageable pageable) {

        Page<CategoryResponseDto> parentCategoryList = categoryService.getParentCategoryList(pageable);

        return ResponseEntity.ok(parentCategoryList);
    }

    @PutMapping("{categoryUuid}")
    ResponseEntity<CategoryResponseDto> updateCategory(Authentication authentication,
                                                       @PathVariable String categoryUuid,
                                                       @RequestBody CategoryUpdateDto updateDto){


        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        CategoryResponseDto categoryResponseDto = categoryService.updateCategory(memberUuid, categoryUuid, updateDto);

        return ResponseEntity.ok(categoryResponseDto);

    }

    @DeleteMapping("{categoryUuid}")
    ResponseEntity<CategoryResponseDto> deleteCategory(Authentication authentication,
                                                        @PathVariable String categoryUuid) {

        CustomUserDetails customUserDetails = getCustomUserDetails(authentication);
        String memberUuid = customUserDetails.getMemberUuid();

        categoryService.deleteCategory(memberUuid,categoryUuid);

        return ResponseEntity.noContent().build();

    }

        private CustomUserDetails getCustomUserDetails(Authentication authentication) {
        return (CustomUserDetails) authentication.getPrincipal();
    }

}
