package com.ip.api.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class SalesRequest {

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class GrowthRateRequest {

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate startDate;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate endDate;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate previousStartDate;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate previousEndDate;


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class  DateRangeRequest {

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate startDate;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate endDate;
    }
}
