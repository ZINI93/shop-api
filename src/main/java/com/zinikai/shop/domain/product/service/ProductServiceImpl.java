package com.zinikai.shop.domain.product.service;

import com.zinikai.shop.domain.product.dto.ProductRequestDto;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import com.zinikai.shop.domain.product.dto.ProductUpdateDto;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;


    // 商品登録
    @Override @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {

        Product savedProduct = Product.builder()
                .name(requestDto.getName())
                .price(requestDto.getPrice())
                .description(requestDto.getDescription())
                .stock(requestDto.getStock())
                .build();

        return productRepository.save(savedProduct).toResponseDto();

    }
    //商品をidで探す
    @Override
    public ProductResponseDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("商品が見つかりません"));

        return product.toResponseDto();
    }

    // 商品 - keyword, price 範囲, search logic
    @Override
    public Page<ProductResponseDto> searchProducts(String keyword, BigDecimal minPrice, BigDecimal maxPrice, String sortField, Pageable pageable) {
        return productRepository.searchProduct(keyword,minPrice,maxPrice,sortField,pageable);
    }


    //　アップデート
    @Override @Transactional
    public ProductResponseDto updateProduct(Long productId, ProductUpdateDto updateDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("商品が見つかりません"));

                product.updateInfo(
                        updateDto.getName(),
                        updateDto.getPrice(),
                        updateDto.getDescription(),
                        updateDto.getStock()
                );
            return productRepository.save(product).toResponseDto();
    }

    //削除
    @Override @Transactional
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)){
            throw new EntityNotFoundException("商品が見つかりません");
        }
        productRepository.deleteById(productId);
    }
}
