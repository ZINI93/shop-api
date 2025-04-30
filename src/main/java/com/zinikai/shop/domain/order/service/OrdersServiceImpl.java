package com.zinikai.shop.domain.order.service;

import com.zinikai.shop.domain.adress.entity.Address;
import com.zinikai.shop.domain.adress.exception.AddressNotFoundException;
import com.zinikai.shop.domain.adress.repository.AddressRepository;
import com.zinikai.shop.domain.cart.entity.Cart;
import com.zinikai.shop.domain.cart.repository.CartRepository;
import com.zinikai.shop.domain.cart.service.CartService;
import com.zinikai.shop.domain.coupon.entity.Coupon;
import com.zinikai.shop.domain.coupon.entity.UserCoupon;
import com.zinikai.shop.domain.coupon.exception.CouponNotFoundException;
import com.zinikai.shop.domain.coupon.exception.UserCouponNotFoundException;
import com.zinikai.shop.domain.coupon.repository.UserCouponRepository;
import com.zinikai.shop.domain.coupon.service.CouponUsageService;
import com.zinikai.shop.domain.member.entity.Member;
import com.zinikai.shop.domain.member.exception.MemberNotFoundException;
import com.zinikai.shop.domain.member.repository.MemberRepository;
import com.zinikai.shop.domain.order.dto.OrderItemRequestDto;
import com.zinikai.shop.domain.order.dto.OrdersRequestDto;
import com.zinikai.shop.domain.order.dto.OrdersResponseDto;
import com.zinikai.shop.domain.order.entity.OrderItem;
import com.zinikai.shop.domain.order.entity.Status;
import com.zinikai.shop.domain.order.entity.Orders;
import com.zinikai.shop.domain.order.exception.OrderArgumentNotMatchException;
import com.zinikai.shop.domain.order.exception.OrderNotFoundException;
import com.zinikai.shop.domain.order.exception.OrderStatusMatchException;
import com.zinikai.shop.domain.order.exception.OutOfAmountException;
import com.zinikai.shop.domain.order.repository.OrderItemRepository;
import com.zinikai.shop.domain.order.repository.OrdersRepository;
import com.zinikai.shop.domain.payment.service.PaymentService;
import com.zinikai.shop.domain.product.dto.ProductResponseDto;
import com.zinikai.shop.domain.product.entity.Product;
import com.zinikai.shop.domain.product.entity.QProduct;
import com.zinikai.shop.domain.product.exception.ProductNotFoundException;
import com.zinikai.shop.domain.product.repository.ProductRepository;
import com.zinikai.shop.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {

    private final OrdersRepository ordersRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final UserCouponRepository userCouponRepository;
    private final OrderItemRepository orderItemRepository;

    private final PaymentService paymentService;
    private final CouponUsageService couponUsageService;
    private final ProductService productService;
    private final CartService cartService;
    private final OrderItemService orderItemService;

    @Override
    public Orders createOrder(Member member, BigDecimal finalAmount, OrdersRequestDto requestDto, String sellerUuid, Address address, BigDecimal discountAmount) {

        log.info("Creating order for memberUuid :{}", member.getMemberUuid());

        return Orders.builder()
                .member(member)
                .totalAmount(finalAmount)
                .status(Status.ORDER_PENDING)
                .paymentMethod(requestDto.getPaymentMethod())
                .sellerUuid(sellerUuid)
                .address(address)
                .discountAmount(discountAmount)
                .build();
    }

    @Override
    @Transactional
    public OrdersResponseDto orderProcess(String memberUuid, OrdersRequestDto requestDto) {

        log.info("Creating order for member UUID:{}", memberUuid);

        List<Product> products = loadProducts(requestDto);
        String sellerUuid = products.get(0).getMember().getMemberUuid();
        productService.validateProduct(products, sellerUuid);

        return orderProcessLogic(memberUuid,requestDto, sellerUuid, () -> calculateTotalAmount(requestDto.getOrderItems()));
    }

    @Override
    @Transactional
    public OrdersResponseDto orderProcessFromCart(String memberUuid, OrdersRequestDto requestDto) {
        log.info("Creating order for member ID:{}", memberUuid);

        List<Cart> carts = loadCarts(memberUuid);
        String sellerUuid = carts.get(0).getProduct().getMember().getMemberUuid();
        cartService.validateCarts(carts, sellerUuid);

        return orderProcessLogic(memberUuid, requestDto, sellerUuid, () -> cartCalculateTotalAmount(carts));
    }

    private OrdersResponseDto orderProcessLogic(String memberUuid, OrdersRequestDto requestDto, String sellerUuid , Supplier<BigDecimal> calculateTotalAmount) {

        Member member = findMember(memberUuid);
        Address address = findAddress(memberUuid);
        UserCoupon userCoupon = findUserCoupon(memberUuid, requestDto.getUserCouponUuid());

        paymentService.validatePaymentMethod(requestDto.getPaymentMethod());

        BigDecimal totalAmount = calculateTotalAmount.get();
        BigDecimal discountAmount = applyCoupon(userCoupon, totalAmount);
        BigDecimal finalAmount = totalAmount.subtract(discountAmount);

        Orders order = createOrder(member, finalAmount, requestDto, sellerUuid, address, discountAmount);
        ordersRepository.save(order);

        orderItemService.decreaseStockByOrderItem(requestDto);

        paymentService.createPayment(member, order);

        if (userCoupon != null) {
            userCoupon.usingCoupon(LocalDateTime.now(), order);
            couponUsageService.createCouponUsage(userCoupon, order, discountAmount);
        }

        List<ProductResponseDto> orderItemProducts = orderItemListResponse(requestDto);

        saveOrderItems(requestDto, member, order);

        return new OrdersResponseDto(finalAmount, order.getStatus(), order.getPaymentMethod(), sellerUuid, order.getOrderUuid(), orderItemProducts, userCoupon.getUserCouponUuid());
    }

    @Override
    public OrdersResponseDto getOrder(String memberUuid, String ordersUuid) {
        Orders order = findByMemberUuidAndOrderUuid(memberUuid, ordersUuid);

        validateOrderOwnerOrders(memberUuid, order);

        return order.toResponseDto();
    }

    @Override
    public Page<OrdersResponseDto> searchOrder(String memberUuid, Status status, LocalDateTime
            starDate, LocalDateTime endDate, BigDecimal minAmount, BigDecimal maxAmount, String sortField, Pageable pageable) {

        log.info("Searching orders for member UUID:{}", memberUuid);

        return ordersRepository.searchOrders(memberUuid, status, starDate, endDate, minAmount, maxAmount, sortField, pageable);
    }

    @Override
    @Transactional
    public OrdersResponseDto cancelOrder(String memberUuid, String orderUuid) {

        log.info("Updating order for member UUID:{}, order UUID:{}", memberUuid, orderUuid);

        Orders order = findByMemberUuidAndOrderUuid(memberUuid, orderUuid);

        orderItemService.refundStockByOrder(order);

        userCouponRepository.findByOrderOrderUuid(order.getOrderUuid())
                .ifPresent(UserCoupon::cancelCoupon);

        validateOrderOwnerOrders(memberUuid, order);

        order.isCancellable(order);
        order.orderUpdateStatus(Status.ORDER_CANCELLED);

        log.info("Updated order:{}", order);

        return order.toResponseDto();
    }

    @Override
    @Transactional
    public void deleteOrder(String memberUuid, String orderUuid) {

        log.info("Deleting order for member UUID:{}, order UUID:{}", memberUuid, orderUuid);

        Orders order = findByMemberUuidAndOrderUuid(memberUuid, orderUuid);

        if (order.getStatus() != Status.ORDER_CANCELLED) {
            throw new OrderStatusMatchException("Can not deleted except for canceled order");
        }

        validateOrderOwnerOrders(memberUuid, order);

        ordersRepository.delete(order);
    }

    @Scheduled(cron = "0 0 * * * ?")
    @Override
    @Transactional
    public void autoCancelPendingOrders() {

        log.info("Running scheduled task: Auto-failed expired pending orders");

        int page = 0;
        int size = 100;
        Page<Orders> ordersPage;

        LocalDateTime limit = LocalDateTime.now().minusHours(24);

        do {
            ordersPage = ordersRepository.findByStatusAndCreatedAtBefore(
                    Status.ORDER_PENDING, limit, PageRequest.of(page, size, Sort.by("id"))
            );
            for (Orders order : ordersPage) {
                try{
                    cancelAndRefund(order);
                }catch (Exception e){
                    log.error("Order cancel processing failed:{}", order.getId(), e);
                }
            }

            page++;

        }while (ordersPage.hasNext());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void cancelAndRefund(Orders order){
        order.orderUpdateStatus(Status.ORDER_CANCELLED);
        refundStock(order);
    }

    private Orders findByMemberUuidAndOrderUuid(String memberUuid, String orderUuid) {
        return ordersRepository.findByMemberMemberUuidAndOrderUuid(memberUuid, orderUuid)
                .orElseThrow(() -> new OrderNotFoundException(memberUuid, orderUuid));
    }

    private void validateOrderOwnerOrders(String memberUuid, Orders orders) {
        if (!Objects.equals(orders.getMember().getMemberUuid(), memberUuid)) {
            throw new OrderArgumentNotMatchException("Member UUID dose not match the order owner");
        }
    }

    private void refundStock(Orders orders) {
        List<OrderItem> orderItems = orderItemRepository.findByOrders(orders);

        orderItems.forEach(orderItem -> {
            Product product = orderItem.getProduct();
            product.refundStock(orderItem.getQuantity());
        });
    }

    public BigDecimal calculateTotalAmount(List<OrderItemRequestDto> orderItems) {

        BigDecimal totalAmount = orderItems.stream().
                map(item -> {
                    Product product = productRepository.findByProductUuid(item.getProductUuid())
                            .orElseThrow(() -> new ProductNotFoundException("Not found product uuid"));

                    return product.getPrice().multiply(new BigDecimal(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new OutOfAmountException("Total amount must be greater then or equal to 0");
        }

        return totalAmount;
    }

    public BigDecimal cartCalculateTotalAmount(List<Cart> cartItems) {
        return cartItems.stream()
                .map(cart -> cart.getProduct().getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<OrderItem> saveOrderItems(OrdersRequestDto requestDto, Member member, Orders order) {

        List<String> productUuids = getProductUuids(requestDto);

        Map<String, Product> productMap = productRepository.findAllByProductUuidIn(productUuids)
                .stream().collect(Collectors.toMap(Product::getProductUuid, Function.identity()));

        List<OrderItem> orderItem = requestDto.getOrderItems().stream().map(item ->
        {
            Product product = productMap.get(item.getProductUuid());
            if (product == null) {
                throw new ProductNotFoundException("Product Not found :" + product.getProductUuid());
            }
            return orderItemService.createAndSaveOrderItem(member, item, order, product);
        }).collect(Collectors.toList());

        return orderItemRepository.saveAll(orderItem);
    }

    private List<String> getProductUuids(OrdersRequestDto requestDto) {
        return requestDto.getOrderItems().stream().map(OrderItemRequestDto::getProductUuid)
                .collect(Collectors.toList());
    }

    private List<Product> loadProducts(OrdersRequestDto requestDto) {
        List<String> productUuids = getProductUuids(requestDto);

        return productRepository.findAllByProductUuidIn(productUuids);
    }


    private BigDecimal applyCoupon(UserCoupon userCoupon, BigDecimal totalAmount) {

        if (userCoupon == null) {
            return BigDecimal.ZERO;
        }

        Coupon coupon = userCoupon.getCoupon();

        if (coupon == null) throw new CouponNotFoundException("Coupon Not found");

        if (totalAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new OutOfAmountException("Minimum order amount for coupon not met.");
        }

        return userCoupon.calculateDiscountAmount(totalAmount, coupon);
    }

    private Member findMember(String memberUuid) {
        return memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with UUID:" + memberUuid));
    }

    private Address findAddress(String memberUuid) {
        return addressRepository.findByMemberMemberUuid(memberUuid)
                .orElseThrow(() -> new AddressNotFoundException("Address not found with member UUID :" + memberUuid));
    }

    private UserCoupon findUserCoupon(String memberUuid, String userCouponUuid) {
        if (userCouponUuid == null) {
            return null;
        }
        UserCoupon userCoupon = userCouponRepository.findByMemberMemberUuidAndUserCouponUuid(memberUuid, userCouponUuid)
                .orElseThrow(() -> new UserCouponNotFoundException("UserCoupon not found with member UUID: " + memberUuid + "userCoupon UUID:" + userCouponUuid));

      if (userCoupon.getUsedAt() != null){
          throw new IllegalArgumentException("Coupon has already been used");
      }

        return userCoupon;
    }

    private List<Cart> loadCarts(String memberUuid) {

        List<Cart> carts = cartRepository.findAllByMemberMemberUuid(memberUuid);

        List<String> cartUuids = carts.stream()
                .map(Cart::getCartUuid)
                .collect(Collectors.toList());

        return cartRepository.findAllByMemberUuidAndCartUuids(memberUuid, cartUuids);
    }

    private List<ProductResponseDto> orderItemListResponse(OrdersRequestDto requestDto) {

        List<String> productUuids = requestDto.getOrderItems().stream().map(OrderItemRequestDto::getProductUuid).collect(Collectors.toList());

        Map<String, Product> productMap = productRepository.findAllByProductUuidIn(productUuids).stream().collect(Collectors.toMap(Product::getProductUuid, Function.identity()));

        return requestDto.getOrderItems().stream().map(orderItem -> {
            String productUuid = orderItem.getProductUuid();
            Product product = productMap.get(productUuid);
            if (product == null){
                throw new ProductNotFoundException("Product not found");
            }
            return new ProductResponseDto(
                    product.getName(),
                    product.getPrice(),
                    product.getDescription(),
                    product.getStock(),
                    product.getProductCondition(),
                    product.getProductMaker(),
                    product.getProductUuid()
            );
        }).collect(Collectors.toList());
    }
}