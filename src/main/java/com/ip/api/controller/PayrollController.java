package com.ip.api.controller;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.auth.AuthUser;
import com.ip.api.domain.User;
import com.ip.api.dto.payroll.PayrollRequest.CreatePayrollDTO;
import com.ip.api.dto.payroll.PayrollResponse.PayrollIdDTO;
import com.ip.api.dto.user.UserResponse.ListForPaging;
import com.ip.api.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
public class PayrollController {
    private final PayrollService payrollService;

    // 급여 등록 - 관리자
    @PostMapping("")
    public ApiResponse<PayrollIdDTO> createPayroll(@RequestBody CreatePayrollDTO request) {
        PayrollIdDTO response = payrollService.createPayroll(request);
        return ApiResponse.of(response);
    }
}
