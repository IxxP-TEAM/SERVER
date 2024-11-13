package com.ip.api.controller;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.auth.AuthUser;
import com.ip.api.domain.Customer;
import com.ip.api.domain.User;
import com.ip.api.dto.customer.CustomerRequest.CustomerDTO;
import com.ip.api.dto.customer.CustomerResponse;
import com.ip.api.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        return customerService.getAllCustomers(pageable);
    }

    //특정 고객사 조회
    @GetMapping("/{id}")
    public ApiResponse<CustomerResponse> getCustomerById(@PathVariable Long id){
        CustomerResponse response = customerService.getCustomerById(id);
            return ApiResponse.of(response);
    }

    //이름으로 고객사 조회
    @GetMapping("/search")
    public Page<CustomerResponse> searchCustomersByName(
            @RequestParam String customerName,
            Pageable pageable) {
        return customerService.searchCustomersByName(customerName, pageable);
    }


    //고객사 삭제
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCustomerById(@PathVariable Long id){
        boolean isDeleted = customerService.deleteCustomerById(id);
        if(isDeleted) {
            return ApiResponse.of("고객사가 성공적으로 삭제되었습니다.");
        }else{return null;}
    }

    //고객사 수정
    @PatchMapping("/{id}")
    public ApiResponse<CustomerResponse> updateCustomer(@PathVariable Long id,
                                                        @AuthUser User user,
                                                        @RequestBody CustomerDTO request){
        CustomerResponse updatedCustomer = customerService.updateCustomer(id,user,request);
        return ApiResponse.of(updatedCustomer);
    }


    // 사업자등록번호 중복 확인 엔드포인트
    @PostMapping("/check-duplicate-registration-number")
    public ApiResponse<Map<String, Boolean>> checkDuplicateRegistrationNumber(@RequestBody Map<String, String> request) {
        String registrationNumber = request.get("registrationNumber");
        boolean isDuplicate = customerService.isRegistrationNumberDuplicate(registrationNumber);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isDuplicate", isDuplicate);

        return ApiResponse.of(response); // 응답에 response 객체를 직접 전달
    }

    // 다수의 고객 ID에 대해 이름을 반환하는 엔드포인트
    @PostMapping("/names")
    public ApiResponse<List<CustomerResponse>> getCustomerNames(@RequestBody Map<String, List<Long>> customerIdsMap) {
        List<Long> customerIds = customerIdsMap.get("customerIds");
        List<CustomerResponse> customers = customerService.getCustomerNamesByIds(customerIds);
        return ApiResponse.of(customers);
    }



}
