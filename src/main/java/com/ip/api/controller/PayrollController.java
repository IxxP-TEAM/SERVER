package com.ip.api.controller;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.auth.AuthUser;
import com.ip.api.domain.User;
import com.ip.api.dto.payroll.PayrollRequest.CreatePayrollDTO;
import com.ip.api.dto.payroll.PayrollResponse.PayrollIdDTO;
import com.ip.api.dto.payroll.PayrollResponse.PersonalPayrollDTO;
import com.ip.api.dto.user.UserResponse.ListForPaging;
import com.ip.api.service.PayrollService;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @PostMapping
    public ApiResponse<PayrollIdDTO> createPayroll(@RequestBody CreatePayrollDTO request) {
        PayrollIdDTO response = payrollService.createPayroll(request);
        return ApiResponse.of(response);
    }

    // 급여 전체 조회 - 관리자
    @GetMapping("/all")
    public ApiResponse<ListForPaging> getPayList(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        ListForPaging response = payrollService.getPayList(page, size);
        return ApiResponse.of(response);
    }

    // 급여 내역 조회
    // 시작~마지막 년도, 월 지정하면 해당 내역 조회
    @GetMapping("/filter")
    public ApiResponse<ListForPaging> getPayInfo(@AuthUser User user,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam String startYearMonth,
                                                 @RequestParam String endYearMonth) {
        YearMonth start = YearMonth.parse(startYearMonth);
        YearMonth end = YearMonth.parse(endYearMonth);
        ListForPaging response = payrollService.getPayInfo(user, page, size, start, end);
        return ApiResponse.of(response);
    }

    // 급여 상세 내역 조회
    @GetMapping("/{payId}")
    public ApiResponse<PersonalPayrollDTO> getPayDetailInfo(@PathVariable long payId) {
        PersonalPayrollDTO response = payrollService.getPayDetailInfo(payId);
        return ApiResponse.of(response);
    }
}
