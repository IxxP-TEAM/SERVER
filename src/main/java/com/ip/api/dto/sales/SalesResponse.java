package com.ip.api.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SalesResponse {

    @Data
    public static class SalesOverviewResponse {
        private BigDecimal totalSales;
        private BigDecimal averageSales;
        private Long totalTransactions;

        public SalesOverviewResponse(BigDecimal totalSales, BigDecimal averageSales, Long totalTransactions) {
            this.totalSales = totalSales;
            this.averageSales = averageSales;
            this.totalTransactions = totalTransactions;
        }

    }

    @Data
    public static class SalesByCustomerResponse {
        private Long customerId;
        private BigDecimal salesAmount;
        private LocalDate salesDate;

        public SalesByCustomerResponse(Long customerId, BigDecimal salesAmount, LocalDate salesDate) {
            this.customerId = customerId;
            this.salesAmount = salesAmount;
            this.salesDate = salesDate;
        }

    }

    @Data
    public static class SalesBySalespersonResponse {
        private Long userId;
        private BigDecimal salesAmount;
        private LocalDate salesDate;

        public SalesBySalespersonResponse(Long userId, BigDecimal salesAmount, LocalDate salesDate) {
            this.userId = userId;
            this.salesAmount = salesAmount;
            this.salesDate = salesDate;
        }
    }

    @Data
    public static class SalesByDateResponse {
        private LocalDate salesDate;
        private BigDecimal totalSalesAmount;

        public SalesByDateResponse(LocalDate salesDate, BigDecimal totalSalesAmount) {
            this.salesDate = salesDate;
            this.totalSalesAmount = totalSalesAmount;
        }

    }

    @Data
    public static class SalesGrowthRateResponse {
        private BigDecimal growthRate;

        public SalesGrowthRateResponse(BigDecimal growthRate) {
            this.growthRate = growthRate;
        }

    }

    @Data
    @AllArgsConstructor
    public static class SalesByCustomerSummaryResponse {
        private String customerId;
        private BigDecimal totalSales;
    }

    @Data
    @AllArgsConstructor
    public static class SalesBySalespersonSummaryResponse {
        private String  userName;
        private BigDecimal totalSales;
    }
    @Data
    @AllArgsConstructor
    public static class MonthlySalesResponse {
        private Integer month;
        private BigDecimal totalSales;
    }

    @Data
    @AllArgsConstructor
    public static class TopCustomerResponse {
        private String customerId;
        private BigDecimal totalSales;
    }

    @Data
    @AllArgsConstructor
    public static class TopSalespersonResponse {
        private String userId;
        private BigDecimal totalSales;
    }

    @Data
    @AllArgsConstructor
    public static class SalesPagedResponse  {
        private LocalDate salesDate;
        private BigDecimal salesAmount;
        private String customerId;
        private String userId;
    }

    @Data
    @AllArgsConstructor
    public static class DailySalesResponse {
        private LocalDate salesDate;
        private BigDecimal totalSales;
    }


}
