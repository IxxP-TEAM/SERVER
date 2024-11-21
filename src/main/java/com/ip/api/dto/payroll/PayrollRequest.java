package com.ip.api.dto.payroll;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PayrollRequest {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePayrollDTO {
        private long userId;
        private int year;
        private int month;

        private int bonus;
        private LocalDate paymentDate;//지급일
    }
}
