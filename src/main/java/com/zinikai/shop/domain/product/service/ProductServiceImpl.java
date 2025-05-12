package com.zinikai.shop.domain.product.service;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import com.zinikai.shop.domain.category.entity.Category;
import com.zinikai.shop.domain.category.entity.ProductCategory;
import com.zinikai.shop.domain.category.exception.CategoryNotFoundException;
import com.zinikai.shop.domain.category.repository.CategoryRepository;
import com.zinikai.shop.domain.category.repository.ProductCategoryRepository;
import com.zinikai.shop.domain.category.service.ProductCategoryService;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.exception.MemberNotFoundException;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.product.dto.ProductImageResponseDto;
import com.zinikai.shop.domain.product.dto.ProductRequestDto;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import com.zinikai.shop.domain.product.dto.ProductUpdateDto;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.entity.ProductImage;
import com.zinikai.shop.domain.product.entity.ProductStatus;
import com.zinikai.shop.domain.product.exception.*;
import com.zinikai.shop.domain.product.repository.ProductImageRepository;
import com.zinikai.shop.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
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

    private final ProductCategoryService productCategoryService;

    @Transactional
    private Product createProduct(Member member, ProductRequestDto requestDto) {

        return Product.builder()
                .name(requestDto.getName())
                .price(requestDto.getPrice())
                .description(requestDto.getDescription())
                .stock(requestDto.getStock())
                .productStatus(ProductStatus.ON_SALE)
                .productCondition(requestDto.getProductCondition())
                .productMaker(requestDto.getProductMaker())
                .member(member)
                .build();
    }

    @Override @Transactional
    public ProductResponseDto createProductProcess(String memberUuid, ProductRequestDto requestDto) {

        log.info("Creating product for member UUID: {}", memberUuid);

        Member member = findByMember(memberUuid);
        Category category = findbyCategory(requestDto);

        validateProductImages(requestDto);

        Product product = createProduct(member, requestDto);
        product.validateStock(requestDto);
        productRepository.save(product);

        List<ProductImage> productImages = saveProductImages(memberUuid, requestDto, product);
        List<String> imageUrls = productImages.stream().map(ProductImage::getImageUrl).collect(Collectors.toList());

        ProductCategory productCategory = productCategoryService.createProductCategory(category, product);
        productCategoryRepository.save(productCategory);

        log.info("created product: {}", product);

        return product.toResponseDto(imageUrls);
    }

    @Override
    public Page<ProductResponseDto> searchProducts(String ownerUuid, String keyword, BigDecimal minPrice, BigDecimal maxPrice, String sortField, Pageable pageable) {

        log.info("Searching product for owner UUID:{}", ownerUuid);

        return productRepository.searchProduct(ownerUuid, keyword, minPrice, maxPrice, sortField, pageable);
    }


    @Override
    public ProductResponseDto getProduct(String productUuid) {

        Product product = findProductByProductUuid(productUuid);

        List<ProductImage> productImages = findProductAllByProductUuid(productUuid);
        List<String> productUuids = productImages.stream().map(ProductImage::getImageUrl).collect(Collectors.toList());

        return new ProductResponseDto(product.getName(),product.getPrice(),product.getDescription(),product.getStock(),product.getProductCondition(),product.getProductMaker(),product.getProductUuid());
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(String memberUuid, String productUuid, ProductUpdateDto updateDto) {

        log.info("Updating product for member UUID :{}, product UUID :{} ", memberUuid, productUuid);

        Product product = findProductByMemberUuidAndProductUuid(memberUuid, productUuid);

        matchOwnerUuidAndProductUuid(memberUuid, productUuid, product);

        product.updateInfo(
                updateDto.getName(),
                updateDto.getPrice(),
                updateDto.getDescription(),
                updateDto.getStock()
        );

        List<ProductImage> newImages = getProductImages(updateDto, product);
        List<String> imageUrls = newImages.stream().map(ProductImage::getImageUrl).collect(Collectors.toList());
        validateUpdateProductImages(newImages);
        productImageRepository.saveAll(newImages);

        log.info("Updated product Uuid:{}", product.getProductUuid());

        return product.toResponseDto(imageUrls);
    }


    @Override
    @Transactional
    public void deleteProduct(String memberUuid, String productUuid) {

        log.info("Deleting product for member UUID: {}, product UUID: {}", memberUuid, productUuid);

        Product product = findProductByMemberUuidAndProductUuid(memberUuid, productUuid);

        matchOwnerUuidAndProductUuid(memberUuid, productUuid, product);

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

    private List<ProductImage> getProductImages(ProductUpdateDto updateDto, Product product) {
        productImageRepository.deleteByProduct(product);

        if (updateDto.getImages() == null){
            return Collections.emptyList();
        }

        return updateDto.getImages().stream()
                .map(img ->
                        ProductImage.builder()
                                .product(product)
                                .imageUrl(img.getImageUrl())
                                .ownerUuid(product.getMember().getMemberUuid())
                                .build()
                ).collect(Collectors.toList());
    }

    private List<ProductImage> saveProductImages(String memberUuid, ProductRequestDto requestDto, Product product) {

        List<ProductImage> images = requestDto.getProductImages().stream()
                .map(imagesDto -> ProductImage.builder()
                        .product(product)
                        .imageUrl(imagesDto.getImageUrl())
                        .ownerUuid(memberUuid)
                        .build())
                .collect(Collectors.toList());

        return productImageRepository.saveAll(images);
    }

    private void validateProductImages(ProductRequestDto requestDto) {

        int newImages = requestDto.getProductImages().size();

        if (newImages > 8) {
            throw new OutOfProductImagesException("Picture can be registered from 1 to 8");
        }
    }

    private void validateUpdateProductImages(List<ProductImage> newImages) {

        if (newImages.size() > 8) {
            throw new OutOfProductImagesException("Picture can be registered from 1 to 8");
        }
    }

    private void matchOwnerUuidAndProductUuid(String ownerUuid, String productUuid, Product product) {
        if (!Objects.equals(product.getMember().getMemberUuid(), ownerUuid)) {
            throw new ProductOwnerNotMatchException("Owner UUID does not match the product owner");
        }
        if (!Objects.equals(product.getProductUuid(), productUuid)) {
            throw new ProductOwnerNotMatchException("Product UUID does not match");
        }
    }

    private Category findbyCategory(ProductRequestDto requestDto) {
        return categoryRepository.findByCategoryUuid(requestDto.getCategoryUuid())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
    }

    private Product findProductByMemberUuidAndProductUuid(String memberUuid, String productUuid) {
        return productRepository.findByMemberMemberUuidAndProductUuid(memberUuid, productUuid)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with member UUiD:" + memberUuid + "product UUID: "+productUuid ));
    }

    private Member findByMember(String memberUuid) {
        return memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new MemberNotFoundException("Member Not found"));
    }

    private Product findProductByProductUuid(String productUuid) {
        return productRepository.findByProductUuid(productUuid)
                .orElseThrow(() -> new ProductNotFoundException("Product Not found"));
    }

    private List<ProductImage> findProductAllByProductUuid(String productUuid) {

        if (productUuid == null){
            return Collections.emptyList();
        }
        return productImageRepository.findAllByProductProductUuid(productUuid);

    }
}
