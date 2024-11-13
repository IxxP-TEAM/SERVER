package com.ip.api.dto.payroll;

import com.ip.api.domain.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

//        private float taxRate;
//        private String deductions;
        // 지급 총액
        private int totalAmount;
        private boolean paymentStatus;
    }
}
