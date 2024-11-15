package com.ip.api.service;

import com.ip.api.domain.SalesHistory;
import com.ip.api.dto.sales.SalesRequest;
import com.ip.api.dto.sales.SalesResponse;
import com.ip.api.repository.SalesHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalesHistoryService {

    @Autowired
    private SalesHistoryRepository salesHistoryRepository;

    // 매출 개요 (Overview)
    public SalesResponse.SalesOverviewResponse getSalesOverview() {
        BigDecimal totalSales = salesHistoryRepository.getTotalSales();
        long totalTransactions = salesHistoryRepository.count();
        BigDecimal averageSales = totalTransactions > 0
                ? totalSales.divide(BigDecimal.valueOf(totalTransactions), MathContext.DECIMAL128)
                : BigDecimal.ZERO;

        return new SalesResponse.SalesOverviewResponse(totalSales, averageSales, totalTransactions);
    }

    // 고객사별 매출 조회
    public List<SalesResponse.SalesByCustomerResponse> getSalesByCustomer(Long customerId) {
        return salesHistoryRepository.findByCustomerId(customerId)
                .stream()
                .map(sales -> new SalesResponse.SalesByCustomerResponse(
                        sales.getCustomer().getCustomerId(),
                        sales.getSalesAmount(),
                        sales.getSalesDate()))
                .collect(Collectors.toList());
    }

    // 사원별 매출 조회
    public List<SalesResponse.SalesBySalespersonResponse> getSalesBySalesperson(Long userId) {
        return salesHistoryRepository.findByUserId(userId)
                .stream()
                .map(sales -> new SalesResponse.SalesBySalespersonResponse(
                        sales.getUser().getUserId(),
                        sales.getSalesAmount(),
                        sales.getSalesDate()))
                .collect(Collectors.toList());
    }

    // 특정 기간별 매출 조회
    public List<SalesResponse.SalesByDateResponse> getSalesByDateRange(SalesRequest.DateRangeRequest request) {
        return salesHistoryRepository.findBySalesDateBetween(request.getStartDate(), request.getEndDate())
                .stream()
                .collect(Collectors.groupingBy(
                        SalesHistory::getSalesDate,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                SalesHistory::getSalesAmount,
                                BigDecimal::add)))
                .entrySet()
                .stream()
                .map(entry -> new SalesResponse.SalesByDateResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    // 매출 성장률 계산
    public SalesResponse.SalesGrowthRateResponse getSalesGrowthRate(SalesRequest.GrowthRateRequest request) {
        BigDecimal currentSales = salesHistoryRepository.findBySalesDateBetween(request.getStartDate(), request.getEndDate())
                .stream()
                .map(SalesHistory::getSalesAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal previousSales = salesHistoryRepository.findBySalesDateBetween(request.getPreviousStartDate(), request.getPreviousEndDate())
                .stream()
                .map(SalesHistory::getSalesAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal growthRate = previousSales.compareTo(BigDecimal.ZERO) > 0
                ? (currentSales.subtract(previousSales)).divide(previousSales, MathContext.DECIMAL128).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return new SalesResponse.SalesGrowthRateResponse(growthRate);
    }

    // 고객사별 총 매출 합계 조회
    public List<SalesResponse.SalesByCustomerSummaryResponse> getTotalSalesByCustomer() {
        List<Object[]> results = salesHistoryRepository.getTotalSalesByCustomer();
        return results.stream()
                .map(result -> new SalesResponse.SalesByCustomerSummaryResponse(
                        (Long) result[0], // customerId
                        (BigDecimal) result[1] // totalSales
                ))
                .collect(Collectors.toList());
    }

    // 사원별 총 매출 합계 조회
    public List<SalesResponse.SalesBySalespersonSummaryResponse> getTotalSalesByUser() {
        List<Object[]> results = salesHistoryRepository.getTotalSalesByUser();
        return results.stream()
                .map(result -> new SalesResponse.SalesBySalespersonSummaryResponse(
                        (Long) result[0], // userId
                        (BigDecimal) result[1] // totalSales
                ))
                .collect(Collectors.toList());
    }

    // 특정 기간 동안 고객사별 총 매출 합계 조회
    public List<SalesResponse.SalesByCustomerSummaryResponse> getTotalSalesByCustomerAndDate(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = salesHistoryRepository.getTotalSalesByCustomerAndDate(startDate, endDate);
        return results.stream()
                .map(result -> new SalesResponse.SalesByCustomerSummaryResponse(
                        (Long) result[0], // customerId
                        (BigDecimal) result[1] // totalSales
                ))
                .collect(Collectors.toList());
    }

    // 특정 기간 동안 사원별 총 매출 합계 조회
    public List<SalesResponse.SalesBySalespersonSummaryResponse> getTotalSalesByUserAndDate(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = salesHistoryRepository.getTotalSalesByUserAndDate(startDate, endDate);
        return results.stream()
                .map(result -> new SalesResponse.SalesBySalespersonSummaryResponse(
                        (Long) result[0], // userId
                        (BigDecimal) result[1] // totalSales
                ))
                .collect(Collectors.toList());
    }

    //월별 통계
    public List<SalesResponse.MonthlySalesResponse> getMonthlySalesStatistics(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = salesHistoryRepository.getMonthlySalesStatistics(startDate, endDate);
        return results.stream()
                .map(result -> new SalesResponse.MonthlySalesResponse(
                        (Integer) result[0], // month
                        (BigDecimal) result[1] // totalSales
                ))
                .collect(Collectors.toList());
    }

    //고객사의 상위 매출 기여도
    public List<SalesResponse.TopCustomerResponse> getTopCustomersBySales(int limit) {
        List<Object[]> results = salesHistoryRepository.getTopCustomersBySales();
        return results.stream()
                .limit(limit)
                .map(result -> new SalesResponse.TopCustomerResponse(
                        (Long) result[0], // customerId
                        (BigDecimal) result[1] // totalSales
                ))
                .collect(Collectors.toList());
    }

    //사원의 상위 매출 기여도
    public List<SalesResponse.TopSalespersonResponse> getTopSalespersonsBySales(int limit) {
        List<Object[]> results = salesHistoryRepository.getTopSalespersonsBySales();
        return results.stream()
                .limit(limit)
                .map(result -> new SalesResponse.TopSalespersonResponse(
                        (Long) result[0], // userId
                        (BigDecimal) result[1] // totalSales
                ))
                .collect(Collectors.toList());
    }

    //매출 페이징 처리
    public Page<SalesResponse.SalesPagedResponse> getPagedSalesData(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SalesHistory> salesPage = salesHistoryRepository.findAllSales(pageable);

        return salesPage.map(sales -> new SalesResponse.SalesPagedResponse(
                sales.getSalesDate(),
                sales.getSalesAmount(),
                sales.getCustomer().getCustomerId(),
                sales.getUser().getUserId()
        ));
    }


}
