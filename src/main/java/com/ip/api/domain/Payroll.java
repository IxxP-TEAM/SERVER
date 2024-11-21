package com.ip.api.domain;

import com.ip.api.domain.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payroll extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payId;
    private double baseSalary;
    private double overtimeHours;  //추가근무시
    private int bonus;
    private String deductions;
    private double totalAmount;
    private boolean paymentStatus;
    private LocalDate paymentDate;
    private double nationPension;//국민연금
    private double healthInsurance;//건강보험
    private double employmentInsurance;//고용보험
    private double industrialAccidentInsurance; //산재보험
    private double incomeTax;//소득세
    private double localTax;//주민세
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;
}
