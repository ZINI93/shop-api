package com.zinikai.shop.domain.cart.service;

import com.zinikai.shop.domain.cart.dto.CartRequestDto;
import com.zinikai.shop.domain.cart.dto.CartResponseDto;
import com.zinikai.shop.domain.cart.dto.CartUpdateDto;
import com.zinikai.shop.domain.cart.entity.Cart;
import com.zinikai.shop.domain.cart.repository.CartRepository;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CartResponseDto createCart(String memberUuid, CartRequestDto requestDto) {

        log.info("Creating cart for member ID :{}", memberUuid);

        Member member = memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found member UUID"));

        Product product = productRepository.findByProductUuid(requestDto.getProductUuid())
                .orElseThrow(() -> new IllegalArgumentException("Not found Product UUID"));

        Cart cart = cartRepository.findByMemberMemberUuidAndProductId(member.getMemberUuid(), product.getId())
                .orElse(null);

        int currentCartItem = cartRepository.countByMember(member);

        int MAX_CART_ITEM = 20;
        if (currentCartItem >= MAX_CART_ITEM) {
            throw new IllegalArgumentException("You can't have more then" + MAX_CART_ITEM + "cartItem");
        }

        int existingCartQuantity = (cart != null) ? cart.getQuantity() : 0;

        validateStockAndQuantity(requestDto.getQuantity(), product, existingCartQuantity);

        if (cart != null) {
            cart.updateQuantity(cart.getQuantity() + requestDto.getQuantity());
            return cartRepository.save(cart).toResponse();
        }

        Cart savedCart = Cart.builder()
                .member(member)
                .product(product)
                .quantity(requestDto.getQuantity())
                .build();

        log.info("Created cart:{}", savedCart);

        return cartRepository.save(savedCart).toResponse();
    }

    @Override
    public Page<CartResponseDto> getCarts(String memberUuid, Pageable pageable) {

        log.info("Searching cart for member UUID:{}", memberUuid);

        return cartRepository.findAllByMemberMemberUuid(memberUuid, pageable);

    }

    @Override
    @Transactional
    public CartResponseDto updateCart(String memberUuid, String cartUuid, CartUpdateDto updateDto) {

        log.info("Updating cart for member UUID:{} , Cart UUID:{}", memberUuid, cartUuid);

        Cart cart = cartRepository.findByMemberMemberUuidAndCartUuid(memberUuid, cartUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found MemberUuid: " + memberUuid + " or CartUUID:" + cartUuid));

        matchMemberUuid(memberUuid, cart);

        validateStockAndQuantity(updateDto.getQuantity(), cart.getProduct(), cart.getQuantity());

        cart.updateQuantity(updateDto.getQuantity());

        log.info("Updated cart:{}", cart);

        return cart.toResponse();
    }

    @Override
    @Transactional
    public void deleteCart(String memberUuid, String cartUuid) {

        log.info("Deleting cart for member UUid:{}, payment UUID:{}", memberUuid, cartUuid);

        Cart cart = cartRepository.findByMemberMemberUuidAndCartUuid(memberUuid, cartUuid)
                .orElseThrow(() -> new IllegalArgumentException("Not found MemberUuid or PaymentUuid"));

        matchMemberUuid(memberUuid, cart);

        cartRepository.delete(cart);
    }

    private void matchMemberUuid(String memberUuid, Cart cart) {
        if (!Objects.equals(cart.getMember().getMemberUuid(), memberUuid)) {
            throw new IllegalArgumentException("Member UUID does not match the cart owner");
        }
    }

    private void validateStockAndQuantity(int requestedQuantity, Product product, int existingCartQuantity) {

        int totalRequested = requestedQuantity + existingCartQuantity;

        if (product.getStock() == 0 || product.getStock() < totalRequested) {
            throw new IllegalArgumentException("Not enough stock. Available: " + product.getStock() + ", Requested: " + requestedQuantity);
        }
        if (requestedQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }
}