package com.ip.api.service;

import com.ip.api.domain.Customer;
import com.ip.api.domain.Orders;
import com.ip.api.domain.User;
import com.ip.api.dto.order.OrderRequest;
import com.ip.api.dto.order.OrderResponse;
import com.ip.api.repository.CustomerRepository;
import com.ip.api.repository.OrdersRepository;
import com.ip.api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // 주문 생성 메서드
    public OrderResponse createOrder(User user, OrderRequest.OrderDTO orderDTO) {
        // DTO를 Orders 엔티티로 변환
        Orders order = orderDTO.toEntity();
        // Customer를 데이터베이스에서 조회하고, 없을 시 예외 처리
        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        // Orders 엔티티에 user와 customer 설정
        order.setUser(user);
        order.setCustomer(customer);
        // 저장 후, 저장된 Orders 엔티티를 OrderResponse로 반환
        Orders savedOrder = ordersRepository.save(order);
        return new OrderResponse(savedOrder);
    }

    // 주문 ID로 조회 메서드
    public OrderResponse getOrderById(Long orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        return new OrderResponse(order); // 주문을 응답 객체로 반환
    }

    // 모든 주문 조회 메서드 (페이징 적용)
    public Page<OrderResponse> getAllOrders(int page, int size) {
        // PageRequest 객체 생성: 페이지 번호와 페이지 크기를 설정합니다.
        Pageable pageable = PageRequest.of(page, size);

        // 페이징된 결과를 OrdersRepository에서 가져옵니다.
        Page<Orders> ordersPage = ordersRepository.findAll(pageable);

        // Orders 객체를 OrderResponse 객체로 매핑하여 반환합니다.
        return ordersPage.map(OrderResponse::new);
    }

    // 주문 수정 메서드
    public OrderResponse updateOrder(Long orderId, User user, OrderRequest.OrderDTO requestDTO) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        // 필요한 필드만 업데이트 (null 또는 기본값이 아닌 경우)
        if (requestDTO.getOrderDate() != null) order.setOrderDate(requestDTO.getOrderDate());
        if (requestDTO.getOrderStatus() != null) order.setOrderStatus(requestDTO.getOrderStatus());
        if (requestDTO.getTotalAmount() != 0) order.setTotalAmount(requestDTO.getTotalAmount());
        if (requestDTO.getPaymentMethod() != null) order.setPaymentMethod(requestDTO.getPaymentMethod());
        if (requestDTO.getPaymentStatus() != null) order.setPaymentStatus(requestDTO.getPaymentStatus());
        if (requestDTO.getShippingAddr() != null) order.setShippingAddr(requestDTO.getShippingAddr());
        if (requestDTO.getShippingSdate() != null) order.setShippingSdate(requestDTO.getShippingSdate());
        if (requestDTO.getShippingStatus() != null) order.setShippingStatus(requestDTO.getShippingStatus());
        if (requestDTO.getDiscountAmount() != 0) order.setDiscountAmount(requestDTO.getDiscountAmount());
        if (requestDTO.getTaxAmount() != 0) order.setTaxAmount(requestDTO.getTaxAmount());
        if (requestDTO.getOrderNote() != null) order.setOrderNote(requestDTO.getOrderNote());

        // 수정된 주문을 저장하고 응답 객체로 반환
        Orders updatedOrder = ordersRepository.save(order);
        return new OrderResponse(updatedOrder);
    }

    // 주문 삭제 메서드
    public boolean deleteOrder(Long orderId) {
        if (ordersRepository.existsById(orderId)) {
            ordersRepository.deleteById(orderId);
            return true; // 삭제 성공 시 true 반환
        }
        return false; // 주문이 존재하지 않을 경우 false 반환
    }
}
