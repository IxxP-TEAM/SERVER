package com.ip.api.controller;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.auth.AuthUser;
import com.ip.api.domain.User;
import com.ip.api.dto.user.UserResponse.ListForPaging;
import com.ip.api.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
public class PayrollController {
    private final PayrollService payrollService;

    // 개인 급여 내역 리스트 조회 (사용자)
    @GetMapping("/personal/list")
    public ApiResponse<ListForPaging> getPayrollList(@AuthUser User user, @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "12") int size,
                                                     @RequestParam Integer year) {
        ListForPaging response = payrollService.getPayrollList(user, page, size, year);
        return ApiResponse.of(response);
    }
}
