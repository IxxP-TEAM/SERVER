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

        orderDTO.getProducts().forEach(productDTO -> {
            Product product = productRepository.findById(productDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));

            OrderProduct orderProduct = OrderProduct.builder()
                    .product(product)
                    .quantity(productDTO.getQuantity())
                   // .price(productDTO.getPrice())
                    .discount(productDTO.getDiscount())
                    .tax(productDTO.getTax())
                  //  .subtotal(productDTO.getPrice().multiply(BigDecimal.valueOf(productDTO.getQuantity())))
                    .order(savedOrder)
                    .build();

            // OrderProduct를 명시적으로 저장
            orderProductRepository.save(orderProduct);

            // Orders 객체에도 추가하여 양방향 관계 유지
            savedOrder.addOrderProduct(orderProduct);
        });

        // 전체 Orders와 연관된 OrderProducts 저장
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
        order.setDiscountAmount(orderDTO.getDiscountAmount());
        order.setTaxAmount(orderDTO.getTaxAmount());
        order.setOrderNote(orderDTO.getOrderNote());
        order.setShippingAddr(orderDTO.getShippingAddr());
        order.setShippingSdate(orderDTO.getShippingSdate());

        // 주문에 포함된 OrderProduct 리스트 업데이트
        order.getOrderProducts().clear(); // 기존 OrderProduct 리스트 초기화

        orderDTO.getProducts().forEach(productDTO -> {
            Product product = productRepository.findById(productDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));

            OrderProduct orderProduct = OrderProduct.builder()
                    .product(product)
                    .quantity(productDTO.getQuantity())
                   // .price(product.getPrice()) // Product의 가격을 가져와서 설정
                    .discount(productDTO.getDiscount())
                    .tax(productDTO.getTax())
                  //  .subtotal(product.getPrice().multiply(BigDecimal.valueOf(productDTO.getQuantity())))
                    .order(order)
                    .build();

            orderProductRepository.save(orderProduct); // OrderProduct 저장
            order.addOrderProduct(orderProduct); // Orders 객체에 OrderProduct 추가
        });

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
