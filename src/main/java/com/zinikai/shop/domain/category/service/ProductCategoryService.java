package com.zinikai.shop.domain.category.service;

import com.zinikai.shop.domain.category.entity.Category;
import com.zinikai.shop.domain.category.entity.ProductCategory;
import com.zinikai.shop.domain.product.entity.Product;

public interface ProductCategoryService {

    ProductCategory createProductCategory(Category category, Product product);
}
