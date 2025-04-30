package com.zinikai.shop.domain.product.service;

import com.zinikai.shop.domain.category.entity.Category;
import com.zinikai.shop.domain.category.entity.ProductCategory;
import com.zinikai.shop.domain.category.repository.CategoryRepository;
import com.zinikai.shop.domain.category.repository.ProductCategoryRepository;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.product.dto.ProductRequestDto;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import com.zinikai.shop.domain.product.dto.ProductUpdateDto;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.entity.ProductImage;
import com.zinikai.shop.domain.product.entity.ProductStatus;
import com.zinikai.shop.domain.product.exception.InvalidSellerException;
import com.zinikai.shop.domain.product.exception.ProductNotFoundException;
import com.zinikai.shop.domain.product.exception.ProductOwnerNotMatchException;
import com.zinikai.shop.domain.product.exception.ProductStatusNotMatchException;
import com.zinikai.shop.domain.product.repository.ProductImageRepository;
import com.zinikai.shop.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Override
    @Transactional
    public ProductResponseDto createProduct(String memberUuid, ProductRequestDto requestDto) {

        log.info("Creating product for member UUID: {}", memberUuid);

        Member member = memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found member ID"));

        if (requestDto.getStock() == null || requestDto.getStock() <= 0) {
            throw new IllegalArgumentException("Stock must be greater than 0");
        }

        int newImageCount = requestDto.getProductImages().size();

        if (newImageCount > 8 || newImageCount < 1) {
            throw new IllegalArgumentException("Picture can be registered from 1 to 8");
        }

        Product product = Product.builder()
                .name(requestDto.getName())
                .price(requestDto.getPrice())
                .description(requestDto.getDescription())
                .stock(requestDto.getStock())
                .productStatus(ProductStatus.ON_SALE)
                .productCondition(requestDto.getProductCondition())
                .productMaker(requestDto.getProductMaker())
                .member(member)
                .build();

        log.info("created product: {}", product);

        ProductResponseDto savedProduct = productRepository.save(product).toResponseDto();

        List<ProductImage> images = requestDto.getProductImages().stream()
                .map(imagesDto -> ProductImage.builder()
                        .product(product)
                        .imageUrl(imagesDto.getImageUrl())
                        .ownerUuid(product.getMember().getMemberUuid())
                        .build())
                .collect(Collectors.toList());

        productImageRepository.saveAll(images);

        Category category = categoryRepository.findByCategoryUuid(requestDto.getCategoryUuid())
                .orElseThrow(() -> new IllegalArgumentException("Not found Category Uuid"));

        ProductCategory productCategory = ProductCategory.builder()
                .category(category)
                .product(product)
                .build();

        productCategoryRepository.save(productCategory);


        return savedProduct;
    }

    @Override
    public Page<ProductResponseDto> searchProducts(String ownerUuid, String keyword, BigDecimal minPrice, BigDecimal maxPrice, String sortField, Pageable pageable) {

        log.info("Searching product for owner UUID:{}", ownerUuid);

        return productRepository.searchProduct(ownerUuid, keyword, minPrice, maxPrice, sortField, pageable);
    }


    @Override
    public ProductResponseDto getProduct(String ownerUuid, String productUuid) {
        Product product = productRepository.findByMemberMemberUuidAndProductUuid(ownerUuid, productUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found owner UUID or product UUID"));

        if (!Objects.equals(product.getMember().getMemberUuid(), ownerUuid)) {
            throw new IllegalArgumentException("Product not match for owner UUID");
        }

        return product.toResponseDto();
    }


    @Override
    @Transactional
    public ProductResponseDto updateProduct(String ownerUuid, String productUuid, ProductUpdateDto updateDto) {

        log.info("Updating product for member UUID :{}, product UUID :{} ", ownerUuid, productUuid);

        Product product = productRepository.findByMemberMemberUuidAndProductUuid(ownerUuid, productUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found owner UUID or product UUID"));

        matchOwnerUuidAndProductUuid(ownerUuid, productUuid, product);

        product.updateInfo(
                updateDto.getName(),
                updateDto.getPrice(),
                updateDto.getDescription(),
                updateDto.getStock()
        );

        log.info("updated product :{}", product);

        return product.toResponseDto();
    }

    @Override
    public List<ProductResponseDto> searchByKeywords(String keywords) {

        return List.of();
    }


    @Override
    @Transactional
    public void deleteProduct(String ownerUuid, String productUuid) {

        log.info("Deleting product for member UUID: {}, product UUID: {}", ownerUuid, productUuid);

        Product product = productRepository.findByMemberMemberUuidAndProductUuid(ownerUuid, productUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found Owner UUID or Product UUID"));

        matchOwnerUuidAndProductUuid(ownerUuid, productUuid, product);

        productRepository.delete(product);
    }

    @Override
    public void validateProduct(List<Product> products, String sellerUuid) {

        if (products.isEmpty()) {
            throw new ProductNotFoundException("No valid products found for the given IDs");
        }
        products.forEach(product -> {
                    if (product.getProductStatus() == ProductStatus.SOLD_OUT) {
                        throw new ProductStatusNotMatchException("Item is SOLD_OUT");
                    }
                    if (!product.getMember().getMemberUuid().equals(sellerUuid)) {
                        throw new InvalidSellerException("All items in the order must be from the same seller.");
                    }
                }
        );
    }

    private static void matchOwnerUuidAndProductUuid(String ownerUuid, String productUuid, Product product) {
        if (!Objects.equals(product.getMember().getMemberUuid(), ownerUuid)) {
            throw new ProductOwnerNotMatchException("Owner UUID does not match the product owner");
        }
        if (!Objects.equals(product.getProductUuid(), productUuid)) {
            throw new ProductOwnerNotMatchException("Product UUID does not match");
        }
    }
}
