package com.ip.api.dto.payroll;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PayrollResponse {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonalPayrollDTO {
        private long payId;
        private String connId;
        private String username;
        private int baseSalary;
        private int overtimePay;
        private int bonus;
        // 소득세
        private int incomeTax;
        // 지방소득세
        private int localIncomeTax;
        // 국민연금
        private int nationalPension;
        //건강보험
        private int healthInsurance;
        //고용보험
        private int employmentInsurance;
        //총공제액
        private int totalDeductions;

        // 지급 총액
        private int totalAmount;
        private boolean paymentStatus;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayrollIdDTO {
        private long payId;
        private long totalLateMinutes;
        private long totalEarlyLeaveMinutes;
        private long totalWorkMinutes;
        private long totalNightWorkMinutes;
        private double baseSalary;
        private double deduction;
        private double nightAllowance;
        private double finalSalary;
    }
}
