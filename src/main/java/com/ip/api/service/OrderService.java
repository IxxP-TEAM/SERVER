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

        BigDecimal totalAmount = BigDecimal.ZERO;         // 총 가격
        BigDecimal totalDiscountAmount = BigDecimal.ZERO; // 총 할인 금액
        BigDecimal totalTaxAmount = BigDecimal.ZERO;      // 총 세액

        // 각 제품에 대한 OrderProduct 생성 및 할인/세액 계산
        for (OrderRequest.OrderProductDTO productDTO : orderDTO.getProducts()) {
            Product product = productRepository.findById(productDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));

            BigDecimal originalPrice = BigDecimal.valueOf(product.getProductPrice());

            // 할인율 적용하여 최종 가격 계산
            BigDecimal discountAmount = originalPrice.multiply(productDTO.getDiscount().divide(BigDecimal.valueOf(100)));
            BigDecimal finalPrice = originalPrice.subtract(discountAmount); // 할인 적용된 가격

            // 제품의 총 가격(total) 계산 (세금 포함)
            BigDecimal quantity = BigDecimal.valueOf(productDTO.getQuantity());
            BigDecimal tax = finalPrice.multiply(quantity).multiply(BigDecimal.valueOf(0.1)); // 총 가격의 10%를 세액으로 계산
            BigDecimal total = finalPrice.multiply(quantity).add(tax); // 세금 포함한 최종 금액

            totalAmount = totalAmount.add(total); // 전체 주문 금액 누적
            totalDiscountAmount = totalDiscountAmount.add(discountAmount.multiply(quantity)); // 전체 주문 할인 누적
            totalTaxAmount = totalTaxAmount.add(tax); // 총 세액 누적

            OrderProduct orderProduct = OrderProduct.builder()
                    .product(product)
                    .quantity(productDTO.getQuantity())
                    .price(finalPrice)            // 할인 적용된 가격 설정
                    .discount(discountAmount)     // 개별 할인 금액
                    .tax(tax)                     // 개별 세액 설정
                    .total(total)              // 세금 포함 최종 금액 저장 (total로 변경 가능)
                    .order(savedOrder)
                    .build();

            orderProductRepository.save(orderProduct); // OrderProduct 저장
            savedOrder.addOrderProduct(orderProduct); // Orders 객체에 OrderProduct 추가
        }

        // 총 가격, 총 할인 금액 및 총 세액 설정
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

        // 기본 주문 정보 업데이트
        order.setOrderDate(orderDTO.getOrderDate());
        order.setOrderNote(orderDTO.getOrderNote());
        order.setShippingAddr(orderDTO.getShippingAddr());
        order.setShippingSdate(orderDTO.getShippingSdate());

        // 기존 OrderProduct 리스트 초기화
        order.getOrderProducts().clear();

        BigDecimal totalAmount = BigDecimal.ZERO;         // 총 가격
        BigDecimal totalDiscountAmount = BigDecimal.ZERO; // 총 할인 금액
        BigDecimal totalTaxAmount = BigDecimal.ZERO;      // 총 세액

        // 각 제품에 대해 OrderProduct 생성 및 할인/세액 계산
        for (OrderRequest.OrderProductDTO productDTO : orderDTO.getProducts()) {
            Product product = productRepository.findById(productDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));

            BigDecimal originalPrice = BigDecimal.valueOf(product.getProductPrice());
            BigDecimal discountAmount = originalPrice.multiply(productDTO.getDiscount().divide(BigDecimal.valueOf(100)));
            BigDecimal finalPrice = originalPrice.subtract(discountAmount); // 할인 적용된 가격

            // 제품의 총 가격(total) 계산 (세금 포함)
            BigDecimal quantity = BigDecimal.valueOf(productDTO.getQuantity());
            BigDecimal tax = finalPrice.multiply(quantity).multiply(BigDecimal.valueOf(0.1)); // 총 가격의 10%를 세액으로 계산
            BigDecimal total = finalPrice.multiply(quantity).add(tax); // 세금 포함한 최종 금액

            totalAmount = totalAmount.add(total); // 전체 주문 금액 누적
            totalDiscountAmount = totalDiscountAmount.add(discountAmount.multiply(quantity)); // 전체 주문 할인 누적
            totalTaxAmount = totalTaxAmount.add(tax); // 총 세액 누적

            OrderProduct orderProduct = OrderProduct.builder()
                    .product(product)
                    .quantity(productDTO.getQuantity())
                    .price(finalPrice)            // 할인 적용된 가격 설정
                    .discount(discountAmount)     // 개별 할인 금액
                    .tax(tax)                     // 개별 세액 설정
                    .total(total)              // 세금 포함 최종 금액 저장 (total로 변경 가능)
                    .order(order)
                    .build();

            orderProductRepository.save(orderProduct); // OrderProduct 저장
            order.addOrderProduct(orderProduct); // Orders 객체에 OrderProduct 추가
        }

        // 총 가격, 총 할인 금액 및 총 세액 설정
        order.setTotalAmount(totalAmount.intValue());
        order.setDiscountAmount(totalDiscountAmount.intValue());
        order.setTaxAmount(totalTaxAmount.intValue());

        Orders updatedOrder = ordersRepository.save(order); // 변경된 Orders 저장
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
