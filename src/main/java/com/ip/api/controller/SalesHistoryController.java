package com.ip.api.controller;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.auth.AuthUser;
import com.ip.api.dto.sales.SalesRequest;
import com.ip.api.dto.sales.SalesResponse;
import com.ip.api.service.SalesHistoryService;
import com.ip.api.domain.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SalesHistoryController {

    @Autowired
    private SalesHistoryService salesHistoryService;


    // 모든 판매 내역 조회 (페이징)
    @GetMapping("/all")
    public ApiResponse<Page<SalesResponse.SalesPagedResponse>> getAllSalesHistory(Pageable pageable) {
        Page<SalesResponse.SalesPagedResponse> response = salesHistoryService.getAllSalesHistory(pageable);
        return ApiResponse.of(response);
    }

    // 매출 개요 조회
    @GetMapping("/overview")
    public ApiResponse<SalesResponse.SalesOverviewResponse> getSalesOverview(@AuthUser User user) {
        SalesResponse.SalesOverviewResponse response = salesHistoryService.getSalesOverview();
        return ApiResponse.of(response);
    }

    // 고객사별 매출 조회
    @GetMapping("/by-customer/{customerId}")
    public ApiResponse<List<SalesResponse.SalesByCustomerResponse>> getSalesByCustomer(@PathVariable Long customerId,
                                                                                       @AuthUser User user) {
        List<SalesResponse.SalesByCustomerResponse> response = salesHistoryService.getSalesByCustomer(customerId);
        return ApiResponse.of(response);
    }

    // 사원별 매출 조회
    @GetMapping("/by-salesperson/{userId}")
    public ApiResponse<List<SalesResponse.SalesBySalespersonResponse>> getSalesBySalesperson(@PathVariable Long userId,
                                                                                             @AuthUser User user) {
        List<SalesResponse.SalesBySalespersonResponse> response = salesHistoryService.getSalesBySalesperson(userId);
        return ApiResponse.of(response);
    }

    // 특정 기간별 매출 조회
    @PostMapping("/by-date")
    public ApiResponse<List<SalesResponse.SalesByDateResponse>> getSalesByDateRange(@AuthUser User user,
                                                                                    @Valid @RequestBody SalesRequest.DateRangeRequest request) {
        List<SalesResponse.SalesByDateResponse> response = salesHistoryService.getSalesByDateRange(request);
        return ApiResponse.of(response);
    }

    // 매출 성장률 조회
    @PostMapping("/growth-rate")
    public ApiResponse<SalesResponse.SalesGrowthRateResponse> getSalesGrowthRate(@AuthUser User user,
                                                                                 @Valid @RequestBody SalesRequest.GrowthRateRequest request) {
        SalesResponse.SalesGrowthRateResponse response = salesHistoryService.getSalesGrowthRate(request);
        return ApiResponse.of(response);
    }

    // 고객사별 총 매출 합계 조회
    @GetMapping("/total-by-customer")
    public ApiResponse<Page<SalesResponse.SalesByCustomerSummaryResponse>> getTotalSalesByCustomer(
            Pageable pageable) {
        Page<SalesResponse.SalesByCustomerSummaryResponse> response = salesHistoryService.getTotalSalesByCustomer(pageable);
        return ApiResponse.of(response);
    }


    // 사원별 총 매출 합계 조회
    @GetMapping("/total-by-salesperson")
    public ApiResponse<Page<SalesResponse.SalesBySalespersonSummaryResponse>> getTotalSalesByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthUser User user) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SalesResponse.SalesBySalespersonSummaryResponse> response = salesHistoryService.getTotalSalesByUser(pageable);
        return ApiResponse.of(response);
    }


    // 특정 기간 동안 고객사별 총 매출 합계 조회
    @PostMapping("/total-by-customer/date")
    public ApiResponse<List<SalesResponse.SalesByCustomerSummaryResponse>> getTotalSalesByCustomerAndDate(
            @AuthUser User user,
            @Valid @RequestBody SalesRequest.DateRangeRequest request) {
        List<SalesResponse.SalesByCustomerSummaryResponse> response =
                salesHistoryService.getTotalSalesByCustomerAndDate(request.getStartDate(), request.getEndDate());
        return ApiResponse.of(response);
    }

    // 특정 기간 동안 사원별 총 매출 합계 조회
    @PostMapping("/total-by-salesperson/date")
    public ApiResponse<List<SalesResponse.SalesBySalespersonSummaryResponse>> getTotalSalesByUserAndDate(
            @AuthUser User user,
            @Valid @RequestBody SalesRequest.DateRangeRequest request) {
        List<SalesResponse.SalesBySalespersonSummaryResponse> response =
                salesHistoryService.getTotalSalesByUserAndDate(request.getStartDate(), request.getEndDate());
        return ApiResponse.of(response);
    }

    //월별 매출 통계
    @PostMapping("/monthly-statistics")
    public ApiResponse<List<SalesResponse.MonthlySalesResponse>> getMonthlySalesStatistics(
            @Valid @RequestBody SalesRequest.DateRangeRequest request) {
        List<SalesResponse.MonthlySalesResponse> response =
                salesHistoryService.getMonthlySalesStatistics(request.getStartDate(), request.getEndDate());
        return ApiResponse.of(response);
    }

    //상위 고객사 매출통계
    @GetMapping("/top-customers")
    public ApiResponse<List<SalesResponse.TopCustomerResponse>> getTopCustomersBySales(
            @RequestParam(defaultValue = "10") int limit) {
        List<SalesResponse.TopCustomerResponse> response = salesHistoryService.getTopCustomersBySales(limit);
        return ApiResponse.of(response);
    }

    //상우 고객 매출통계
    @GetMapping("/top-salespersons")
    public ApiResponse<List<SalesResponse.TopSalespersonResponse>> getTopSalespersonsBySales(
            @RequestParam(defaultValue = "10") int limit) {
        List<SalesResponse.TopSalespersonResponse> response = salesHistoryService.getTopSalespersonsBySales(limit);
        return ApiResponse.of(response);
    }

    @PostMapping("/daily-statistics")
    public ApiResponse<List<SalesResponse.DailySalesResponse>> getDailySalesStatistics(
            @Valid @RequestBody SalesRequest.DateRangeRequest request) {
        List<SalesResponse.DailySalesResponse> response =
                salesHistoryService.getDailySalesStatistics(request.getStartDate(), request.getEndDate());
        return ApiResponse.of(response);
    }
}
