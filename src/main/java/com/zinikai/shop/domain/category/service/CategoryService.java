package com.zinikai.shop.domain.category.service;

import com.zinikai.shop.domain.category.dto.CategoryRequestDto;
import com.zinikai.shop.domain.category.dto.CategoryResponseDto;
import com.zinikai.shop.domain.category.dto.CategoryUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    CategoryResponseDto createCategory(String memberUuid, CategoryRequestDto requestDto);

    CategoryResponseDto getCategoryInfo(String memberUuid, String categoryUuid);

    Page<CategoryResponseDto> getCategoryList(String memberUuid, Pageable pageable);

    Page<CategoryResponseDto> getParentCategoryList(Pageable pageable);


    CategoryResponseDto updateCategory(String memberUuid, String categoryUuid, CategoryUpdateDto updateDto);

    void deleteCategory(String memberUuid, String categoryUuid);
}
