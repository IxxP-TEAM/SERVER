package com.ip.api.service;

import com.ip.api.domain.*;
import com.ip.api.dto.order.OrderRequest;
import com.ip.api.dto.order.OrderResponse;
import com.ip.api.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    // 주문 생성 (여러 제품 추가)
    @Transactional
    public OrderResponse createOrderWithProducts(OrderRequest.OrderDTO orderDTO) {
        Orders order = orderDTO.toEntity();

        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        order.setUser(user);
        order.setCustomer(customer);

        Orders savedOrder = ordersRepository.save(order);

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalDiscountAmount = BigDecimal.ZERO;
        BigDecimal totalTaxAmount = BigDecimal.ZERO;

        for (OrderRequest.OrderProductDTO productDTO : orderDTO.getProducts()) {
            Product product = productRepository.findById(productDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));

            BigDecimal originalPrice = BigDecimal.valueOf(product.getProductPrice());
            BigDecimal quantity = BigDecimal.valueOf(productDTO.getQuantity());

            // 1. 부가세 적용된 금액 계산
            BigDecimal tax = originalPrice.multiply(quantity).multiply(BigDecimal.valueOf(0.1));
            BigDecimal priceWithTax = originalPrice.multiply(quantity).add(tax);

            // 2. 부가세 적용된 금액에 대한 할인 금액 계산 및 최종 가격 설정
            BigDecimal discountAmount = priceWithTax.multiply(productDTO.getDiscount().divide(BigDecimal.valueOf(100)));
            BigDecimal finalPrice = priceWithTax.subtract(discountAmount);

            totalAmount = totalAmount.add(finalPrice);
            totalDiscountAmount = totalDiscountAmount.add(discountAmount);
            totalTaxAmount = totalTaxAmount.add(tax);

            OrderProduct orderProduct = OrderProduct.builder()
                    .product(product)
                    .quantity(productDTO.getQuantity())
                    .price(originalPrice)
                    .discount(discountAmount)
                    .tax(tax)
                    .total(finalPrice)
                    .order(savedOrder)
                    .build();

            orderProductRepository.save(orderProduct);
            savedOrder.addOrderProduct(orderProduct);
        }

        order.setTotalAmount(totalAmount.intValue());
        order.setDiscountAmount(totalDiscountAmount.intValue());
        order.setTaxAmount(totalTaxAmount.intValue());

        Orders finalSavedOrder = ordersRepository.save(savedOrder);
        return new OrderResponse(finalSavedOrder);
    }



    // 주문 조회
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Orders order = ordersRepository.findByIdWithProducts(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        return new OrderResponse(order);
    }
    // 전체 주문 조회 (페이징 적용)
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Orders> ordersPage = ordersRepository.findAll(pageable);

        // Orders -> OrderResponse로 변환
        return ordersPage.map(OrderResponse::new);
    }
    // 주문 수정
    @Transactional
    public OrderResponse updateOrder(Long orderId, OrderRequest.OrderDTO orderDTO) {
        Orders order = ordersRepository.findByIdWithProducts(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        order.setOrderDate(orderDTO.getOrderDate());
        order.setOrderNote(orderDTO.getOrderNote());
        order.setShippingAddr(orderDTO.getShippingAddr());
        order.setShippingSdate(orderDTO.getShippingSdate());

        order.getOrderProducts().clear();

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalDiscountAmount = BigDecimal.ZERO;
        BigDecimal totalTaxAmount = BigDecimal.ZERO;

        for (OrderRequest.OrderProductDTO productDTO : orderDTO.getProducts()) {
            Product product = productRepository.findById(productDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));

            BigDecimal originalPrice = BigDecimal.valueOf(product.getProductPrice());
            BigDecimal quantity = BigDecimal.valueOf(productDTO.getQuantity());

            BigDecimal tax = originalPrice.multiply(quantity).multiply(BigDecimal.valueOf(0.1));
            BigDecimal priceWithTax = originalPrice.multiply(quantity).add(tax);

            BigDecimal discountAmount = priceWithTax.multiply(productDTO.getDiscount().divide(BigDecimal.valueOf(100)));
            BigDecimal finalPrice = priceWithTax.subtract(discountAmount);

            totalAmount = totalAmount.add(finalPrice);
            totalDiscountAmount = totalDiscountAmount.add(discountAmount);
            totalTaxAmount = totalTaxAmount.add(tax);

            OrderProduct orderProduct = OrderProduct.builder()
                    .product(product)
                    .quantity(productDTO.getQuantity())
                    .price(originalPrice)
                    .discount(discountAmount)
                    .tax(tax)
                    .total(finalPrice)
                    .order(order)
                    .build();

            orderProductRepository.save(orderProduct);
            order.addOrderProduct(orderProduct);
        }

        order.setTotalAmount(totalAmount.intValue());
        order.setDiscountAmount(totalDiscountAmount.intValue());
        order.setTaxAmount(totalTaxAmount.intValue());

        Orders updatedOrder = ordersRepository.save(order);
        return new OrderResponse(updatedOrder);
    }

    // 주문 삭제
    @Transactional
    public boolean deleteOrder(Long orderId) {
        if (ordersRepository.existsById(orderId)) {
            ordersRepository.deleteById(orderId);
            return true;
        }
        return false;
    }
}
