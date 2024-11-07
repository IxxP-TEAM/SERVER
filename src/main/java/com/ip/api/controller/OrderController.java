package com.ip.api.controller;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.auth.AuthUser;
import com.ip.api.domain.User;
import com.ip.api.dto.order.OrderRequest.OrderDTO;
import com.ip.api.dto.order.OrderResponse;
import com.ip.api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService ordersService;

    //주문 생성
    @PostMapping("/create")
    public ApiResponse<OrderResponse> createOrder(@AuthUser User user, @RequestBody OrderDTO request){
        OrderResponse response = ordersService.createOrder(user,request);
        return ApiResponse.of(response);
    }

    //전체 주문 조회
    @GetMapping("/all")
    public ApiResponse<List<OrderResponse>> getAllOrders(){
        List<OrderResponse> response = ordersService.getAllOrders();
        return ApiResponse.of(response);
    }

    //특정 주문 조회
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable Long id){
        OrderResponse response = ordersService.getOrderById(id);
        return ApiResponse.of(response);
    }

    //주문 수정
    @PatchMapping("/{id}")
    public ApiResponse<OrderResponse> updateOrder(@PathVariable Long id,@AuthUser User user, @RequestBody OrderDTO request){

        OrderResponse updatedOrder = ordersService.updateOrder(id,user,request);
        return ApiResponse.of(updatedOrder);
    }

    //주문 삭제
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteOrderById(@PathVariable Long id){
        boolean isDeleted = ordersService.deleteOrder(id);
        if(isDeleted){
            return ApiResponse.of("고객사가 성공적으로 삭제되었습니다.");
        }
        else{return null;}
    }
}
