package com.ip.api.controller;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.auth.AuthUser;
import com.ip.api.domain.Customer;
import com.ip.api.domain.User;
import com.ip.api.dto.customer.CustomerRequest.CustomerDTO;
import com.ip.api.dto.customer.CustomerResponse;
import com.ip.api.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // 고객사 등록
    @PostMapping("/create")
    public ApiResponse<CustomerResponse> createCustomer(@AuthUser User user, @RequestBody CustomerDTO request) {
        CustomerResponse response = customerService.createCustomer(user, request);
        return ApiResponse.of(response);
    }

    //모든 고객사 조회
    @GetMapping("/all")
    public ApiResponse<List<CustomerResponse>> getAllCustomers(){
        List<CustomerResponse> response = customerService.getAllCustomers();
        return ApiResponse.of(response);
    }

    //특정 고객사 조회
    @GetMapping("/{id}")
    public ApiResponse<CustomerResponse> getCustomerById(@PathVariable Long id){
        CustomerResponse response = customerService.getCustomerById(id);
            return ApiResponse.of(response);
    }

    //이름으로 고객사 조회
    @GetMapping("/search")
    public ApiResponse<List<CustomerResponse>> searchCustomerByName(@RequestParam String name){
        List<CustomerResponse> response = customerService.searchCustomersByName(name);
        return ApiResponse.of(response);
    }


    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCustomerById(@PathVariable Long id){
        boolean isDeleted = customerService.deleteCustomerById(id);
        if(isDeleted) {
            return ApiResponse.of("고객사가 성공적으로 삭제되었습니다.");
        }else{return null;}
    }
}
