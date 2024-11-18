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

    // 모든 판매 내역 조회 (페이징)
    public Page<SalesResponse.SalesPagedResponse> getAllSalesHistory(Pageable pageable) {
        return salesHistoryRepository.findAll(pageable)
                .map(salesHistory -> new SalesResponse.SalesPagedResponse(
                        salesHistory.getSalesDate(),
                        salesHistory.getSalesAmount(),
                        salesHistory.getCustomer().getCustomerName(),
                        salesHistory.getUser().getUserName()
                ));
    }

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

    // 고객사별 총 매출 합계 조회 (고객사 이름 포함)
    public Page<SalesResponse.SalesByCustomerSummaryResponse> getTotalSalesByCustomer(Pageable pageable) {
        Page<Object[]> results = salesHistoryRepository.getTotalSalesByCustomerWithName(pageable);
        return results.map(result -> new SalesResponse.SalesByCustomerSummaryResponse(
                (String) result[0], // customerName
                (BigDecimal) result[1] // totalSales
        ));
    }


    // 사원별 총 매출 합계 조회
    public Page<SalesResponse.SalesBySalespersonSummaryResponse> getTotalSalesByUser(Pageable pageable) {
        Page<Object[]> results = salesHistoryRepository.getTotalSalesByUser(pageable);
        return results.map(result -> new SalesResponse.SalesBySalespersonSummaryResponse(
                (String) result[0], // userName
                (BigDecimal) result[1] // totalSales
        ));
    }



    // 특정 기간 동안 고객사별 총 매출 합계 조회
    public List<SalesResponse.SalesByCustomerSummaryResponse> getTotalSalesByCustomerAndDate(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = salesHistoryRepository.getTotalSalesByCustomerAndDate(startDate, endDate);
        return results.stream()
                .map(result -> new SalesResponse.SalesByCustomerSummaryResponse(
                        (String) result[0], // customerId
                        (BigDecimal) result[1] // totalSales
                ))
                .collect(Collectors.toList());
    }

    // 특정 기간 동안 사원별 총 매출 합계 조회
    public List<SalesResponse.SalesBySalespersonSummaryResponse> getTotalSalesByUserAndDate(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = salesHistoryRepository.getTotalSalesByUserAndDate(startDate, endDate);
        return results.stream()
                .map(result -> new SalesResponse.SalesBySalespersonSummaryResponse(
                        (String) result[0], // userId
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

    public List<SalesResponse.DailySalesResponse> getDailySalesStatistics(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = salesHistoryRepository.getDailySalesStatistics(startDate, endDate);
        return results.stream()
                .map(result -> new SalesResponse.DailySalesResponse(
                        (LocalDate) result[0], // 날짜
                        (BigDecimal) result[1] // 총 매출
                ))
                .collect(Collectors.toList());
    }

}
