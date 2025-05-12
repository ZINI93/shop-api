package com.zinikai.shop.domain.category.service;


import com.zinikai.shop.domain.category.entity.Category;
import com.zinikai.shop.domain.category.entity.ProductCategory;
import com.zinikai.shop.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {
    @Override
    public ProductCategory createProductCategory(Category category, Product product) {

        return  ProductCategory.builder()
                .category(category)
                .product(product)
                .build();

    }
}
