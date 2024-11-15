package com.ip.api.service;

import com.ip.api.domain.*;
import com.ip.api.domain.enums.OrderStatus;
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

    @Autowired
    private SalesHistoryRepository salesHistoryRepository;

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
                    .order(savedOrder)
                    .build();

            orderProductRepository.save(orderProduct);
            savedOrder.addOrderProduct(orderProduct);
        }

        order.setTotalAmount(totalAmount.intValue());
        order.setDiscountAmount(totalDiscountAmount.intValue());
        order.setTaxAmount(totalTaxAmount.intValue());

        Orders finalSavedOrder = ordersRepository.save(savedOrder);

        if (order.getOrderStatus() == OrderStatus.완료) {
            SalesHistory salesHistory = SalesHistory.builder()
                    .salesAmount(BigDecimal.valueOf(order.getTotalAmount()))
                    .salesDate(order.getOrderDate())
                    .user(order.getUser())
                    .customer(order.getCustomer())
                    .order(finalSavedOrder)
                    .build();

            salesHistoryRepository.save(salesHistory);
        }

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

        // 주문 정보 업데이트
        order.setOrderDate(orderDTO.getOrderDate());
        order.setOrderStatus(orderDTO.getOrderStatus());
        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setPaymentStatus(orderDTO.getPaymentStatus());
        order.setShippingAddr(orderDTO.getShippingAddr());
        order.setShippingSdate(orderDTO.getShippingSdate());
        order.setShippingStatus(orderDTO.getShippingStatus());
        order.setDiscountAmount(orderDTO.getDiscountAmount());
        order.setTaxAmount(orderDTO.getTaxAmount());
        order.setOrderNote(orderDTO.getOrderNote());


        order.getOrderProducts().clear();

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalDiscountAmount = BigDecimal.ZERO;
        BigDecimal totalTaxAmount = BigDecimal.ZERO;

        for (OrderRequest.OrderProductDTO productDTO : orderDTO.getProducts()) {
            Product product = productRepository.findById(productDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));

            BigDecimal originalPrice = BigDecimal.valueOf(product.getProductPrice());
            BigDecimal quantity = BigDecimal.valueOf(productDTO.getQuantity());

            BigDecimal productTotal = originalPrice.multiply(quantity);
            BigDecimal tax = productTotal.multiply(BigDecimal.valueOf(0.1));
            BigDecimal priceWithTax = productTotal.add(tax);
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

        // **SalesHistory 생성 또는 업데이트 로직**
        if (order.getOrderStatus() == OrderStatus.완료) {
            // 주문이 완료 상태일 때 SalesHistory 조회 후 없으면 생성
            SalesHistory salesHistory = salesHistoryRepository.findByOrderOrderId(orderId)
                    .orElse(SalesHistory.builder()
                            .order(updatedOrder)
                            .user(order.getUser())
                            .customer(order.getCustomer())
                            .build());

            // SalesHistory 데이터 업데이트
            salesHistory.setSalesAmount(BigDecimal.valueOf(order.getTotalAmount()));
            salesHistory.setSalesDate(order.getOrderDate());
            salesHistoryRepository.save(salesHistory);
        } else {
            // 주문이 완료 상태가 아닐 때 SalesHistory 삭제
            salesHistoryRepository.deleteByOrderOrderId(orderId);
        }

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
