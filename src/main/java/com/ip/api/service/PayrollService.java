package com.ip.api.service;

import com.ip.api.domain.Payroll;
import com.ip.api.domain.User;
import com.ip.api.dto.payroll.PayrollResponse.PersonalPayrollDTO;
import com.ip.api.dto.user.UserResponse.ListForPaging;
import com.ip.api.repository.PayrollRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayrollService {
    private final PayrollRepository payrollRepository;

public ListForPaging getPayrollList(User user, int page, int size, int year) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Payroll> response = payrollRepository.findByCreateAtYearAndUser(year, user, pageable);

    List<PersonalPayrollDTO> payrollDTOList = response.getContent().stream().map(payroll -> {
        int baseSalary = payroll.getBaseSalary();
//        int overtimeHours = (int) Duration.between(payroll.getOvertimeHours(), LocalDateTime.now()).toHours(); // 초과 근무 시간
        int hourlyRate = 9860; // 시급을 적절한 값으로 설정
        int overtimePay = calculateOvertimePay(payroll.getOvertimeHours(), hourlyRate); // 초과 근무 수당
        int bonus = payroll.getBonus(); // 보너스

        // 공제 항목 계산
        int incomeTax = calculateIncomeTax(baseSalary, bonus, overtimePay);
        int localIncomeTax = calculateLocalIncomeTax(incomeTax);
        int nationalPension = calculateNationalPension(baseSalary);
        int healthInsurance = calculateHealthInsurance(baseSalary);
        int employmentInsurance = calculateEmploymentInsurance(baseSalary);

        // 총 공제액
        int totalDeductions = incomeTax + localIncomeTax  + nationalPension + healthInsurance + employmentInsurance;

        // 지급 총액
        int totalAmount = baseSalary + overtimePay + bonus - totalDeductions;

        return PersonalPayrollDTO.builder()
                .payId(payroll.getPayId())
                .connId(user.getConnId())
                .username(user.getUserName())
                .baseSalary(baseSalary)
                .overtimePay(overtimePay)
                .bonus(bonus)
                .incomeTax(incomeTax)
                .localIncomeTax(localIncomeTax)
                .nationalPension(nationalPension)
                .healthInsurance(healthInsurance)
                .employmentInsurance(employmentInsurance)
                .totalDeductions(totalDeductions)
                .totalAmount(totalAmount)
                .paymentStatus(payroll.isPaymentStatus())
                .build();
    }).collect(Collectors.toList());

    return ListForPaging.<PersonalPayrollDTO>builder()
            .totalElements(response.getTotalElements())
            .totalPages(response.getTotalPages())
            .pageSize(size)
            .currentPage(page)
            .items(payrollDTOList)
            .build();
}

    private int calculateIncomeTax(int baseSalary, int bonus, int overtimePay) {
        return (int) (0.1 * (baseSalary + bonus + overtimePay));
    }

    private int calculateLocalIncomeTax(int incomeTax) {
        return (int) (incomeTax * 0.1);
    }

    private int calculateNationalPension(int baseSalary) {
        return (int) (baseSalary * 0.045);
    }

    private int calculateHealthInsurance(int baseSalary) {
        return (int) (baseSalary * 0.035);
    }

    private int calculateEmploymentInsurance(int baseSalary) {
        return (int) (baseSalary * 0.009);
    }

    private int calculateOvertimePay(int overtimeHours, int hourlyRate) {
        double overtimeMultiplier = 1.5;
        double overtimePay = overtimeHours * hourlyRate * overtimeMultiplier;

        return (int) Math.round(overtimePay);
    }
}
