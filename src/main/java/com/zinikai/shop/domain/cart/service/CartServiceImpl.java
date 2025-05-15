package com.zinikai.shop.domain.cart.service;

import com.zinikai.shop.domain.cart.dto.CartRequestDto;
import com.zinikai.shop.domain.cart.dto.CartResponseDto;
import com.zinikai.shop.domain.cart.dto.CartUpdateDto;
import com.zinikai.shop.domain.cart.entity.Cart;
import com.zinikai.shop.domain.cart.exception.CartNotFoundException;
import com.zinikai.shop.domain.cart.exception.ValidateCartException;
import com.zinikai.shop.domain.cart.repository.CartRepository;
import com.zinikai.shop.domain.coupon.exception.ValidateUserCouponException;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.exception.MemberNotFoundException;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.exception.OutOfQuantityException;
import com.zinikai.shop.domain.product.exception.OutOfStockException;
import com.zinikai.shop.domain.product.exception.ProductNotFoundException;
import com.zinikai.shop.domain.product.exception.ValidateProductException;
import com.zinikai.shop.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.zinikai.shop.domain.cart.entity.QCart.cart;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;


    @Override
    public Cart createCart(Member member, Product product, CartRequestDto requestDto) {
        return Cart.builder()
                .member(member)
                .product(product)
                .quantity(requestDto.getQuantity())
                .build();
    }

    @Override @Transactional
    public CartResponseDto createCartWithValidate(String memberUuid, CartRequestDto requestDto) {

        log.info("Creating cart for member ID :{}", memberUuid);

        Member member = findMemberByMemberUuid(memberUuid);
        Product product = findProductByProductUuid(requestDto);

        validateCartInItems(member, product);

        Cart existingCart = findCartByMemberAndProduct(member, product);

        if (existingCart != null) {
            return updateCartQuantity(requestDto, existingCart, product);
        }

        validateStockAndQuantity(requestDto.getQuantity(), product, 0);
        Cart cart = createCart(member, product, requestDto);

        Cart savedCart = cartRepository.save(cart);

        log.info("Created cart:{}", savedCart);

        return savedCart.toResponse();

    }


    @Override
    public Page<CartResponseDto> getCarts(String memberUuid, Pageable pageable) {

        log.info("Searching cart for member UUID:{}", memberUuid);

        return cartRepository.findAllByMemberMemberUuid(memberUuid, pageable);

    }

    @Override @Transactional
    public CartResponseDto updateCart(String memberUuid, String cartUuid, CartUpdateDto updateDto) {

        log.info("Updating cart for member UUID:{} , Cart UUID:{}", memberUuid, cartUuid);

        Cart cart = findCartByMemberUuidAndCartUuid(memberUuid, cartUuid);

        matchMemberUuid(memberUuid, cart);

        validateStockAndQuantity(updateDto.getQuantity(), cart.getProduct(), cart.getQuantity());

        cart.updateQuantity(updateDto.getQuantity());

        log.info("Updated cart:{}", cart);

        return cart.toResponse();
    }

    @Override @Transactional
    public void deleteCart(String memberUuid, String cartUuid) {

        log.info("Deleting cart for member UUid:{}, payment UUID:{}", memberUuid, cartUuid);

        Cart cart = findCartByMemberUuidAndCartUuid(memberUuid,cartUuid);

        matchMemberUuid(memberUuid, cart);

        cartRepository.delete(cart);
    }

    @Override
    public void validateCarts(List<Cart> carts, String sellerUuid) {

        if (carts.isEmpty()) {
            throw new CartNotFoundException("No valid cart item selected for order");
        }

        boolean isValidSeller = carts.stream()
                .allMatch(cart -> cart.getProduct().getMember().getMemberUuid().equals(sellerUuid));

        if (!isValidSeller) {
            throw new ValidateCartException("All items in the order must be from the same seller.");
        }
    }

    private CartResponseDto updateCartQuantity(CartRequestDto requestDto, Cart existingCart, Product product) {
        int totalQuantity = existingCart.getQuantity() + requestDto.getQuantity();
        validateStockAndQuantity(requestDto.getQuantity(), product, existingCart.getQuantity());
        existingCart.updateQuantity(totalQuantity);
        return cartRepository.save(existingCart).toResponse();
    }

    public static final int MAX_CART_ITEM = 20;

    private void validateCartInItems(Member member, Product product) {

        int countByCart = cartRepository.countByCart(product, member);

        if (countByCart >= MAX_CART_ITEM) {
            throw new IllegalArgumentException("Cart items limit exceeded");
        }
    }

    private void matchMemberUuid(String memberUuid, Cart cart) {
        if (!Objects.equals(cart.getMember().getMemberUuid(), memberUuid)) {
            throw new IllegalArgumentException("Member UUID does not match the cart owner");
        }
    }

    private void validateStockAndQuantity(int requestedQuantity, Product product, int existingCartQuantity) {

        int totalRequested = requestedQuantity + existingCartQuantity;

        if (requestedQuantity <= 0) {
            throw new OutOfQuantityException("Quantity must be greater than 0");
        }
        
        if (product.getStock() == 0 || product.getStock() < totalRequested) {
            throw new OutOfStockException("Not enough stock. Available: " + product.getStock() + ", Requested: " + requestedQuantity);
        }
    }

    private Member findMemberByMemberUuid(String memberUuid) {
        return memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new MemberNotFoundException("Not found member UUID"));
    }

    private Product findProductByProductUuid(CartRequestDto requestDto) {
        return productRepository.findByProductUuid(requestDto.getProductUuid())
                .orElseThrow(() -> new ProductNotFoundException("Not found Product UUID"));
    }

    private Cart findCartByMemberAndProduct(Member member, Product product) {
        return cartRepository.findByMemberMemberUuidAndProductId(member.getMemberUuid(), product.getId())
                .orElse(null);
    }

    private Cart findCartByMemberUuidAndCartUuid(String memberUuid, String cartUuid) {
        return cartRepository.findByMemberMemberUuidAndCartUuid(memberUuid, cartUuid)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for member UUID: " + memberUuid + "cart UUID" +cartUuid));
    }
}