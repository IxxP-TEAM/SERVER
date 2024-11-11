package com.ip.api.controller;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.auth.AuthUser;
import com.ip.api.domain.User;
import com.ip.api.dto.order.OrderRequest.OrderDTO;
import com.ip.api.dto.order.OrderResponse;
import com.ip.api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 주문 생성 (여러 제품 포함)
    @PostMapping("/create")
    public ApiResponse<OrderResponse> createOrderWithProducts(@AuthUser User user, @RequestBody OrderDTO request) {
        OrderResponse response = orderService.createOrderWithProducts(request);
        return ApiResponse.of(response);
    }

    // 전체 주문 조회 (페이징 적용)
    @GetMapping("/all")
    public ApiResponse<Page<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OrderResponse> response = orderService.getAllOrders(page, size);
        return ApiResponse.of(response);
    }

    // 특정 주문 조회
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ApiResponse.of(response);
    }

    // 주문 수정
    @PatchMapping("/{id}")
    public ApiResponse<OrderResponse> updateOrder(
            @PathVariable Long id,
            @AuthUser User user,
            @RequestBody OrderDTO request
    ) {
        OrderResponse updatedOrder = orderService.updateOrder(id, request);
        return ApiResponse.of(updatedOrder);
    }

    // 주문 삭제
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteOrderById(@PathVariable Long id) {
        boolean isDeleted = orderService.deleteOrder(id);
        if (isDeleted) {
            return ApiResponse.of("주문이 성공적으로 삭제되었습니다.");
        } else {
            return ApiResponse.of("주문 삭제에 실패했습니다.");
        }
    }
}
