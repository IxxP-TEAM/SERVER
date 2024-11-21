package com.ip.api.dto.payroll;

import com.ip.api.domain.enums.Department;
import java.time.LocalDateTime;
import java.time.YearMonth;
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
        private Department department;
        private LocalDateTime month;
        private String jobTitle;
        private String baseSalary;//기본급
        private String overtimePay;//야간근로수당
        private String bonus;//보너스
        // 소득세111
        private String incomeTax;// 소득세
        private String localIncomeTax;// 지방소득세(주민세)
        private String nationalPension;//국민연금
        private String healthInsurance;//건강보험
        private String employmentInsurance;//고용보험
//        private int totalDeductions;

        // 지급 총액
        private String totalAmount;
        private String absentDeduction;//결근/조퇴으로 차감된
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
