package com.ip.api.controller;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.auth.AuthUser;
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
    public List<CustomerResponse> getAllCustomers(){
        return customerService.getAllCustomers();
    }

    //특정 고객사 조회
    @GetMapping("/{id}")
    public CustomerResponse getCustomerById(@PathVariable Long id){
        return customerService.getCustomerById(id);
    }

    //이름으로 고객사 조회
    @GetMapping("/search")
    public List<CustomerResponse> searchCustomerByName (@RequestParam String name){
        return customerService.searchCustomersByName(name);
    }
}
