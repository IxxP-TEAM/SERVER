package com.ip.api.domain;

import com.ip.api.domain.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payHistoryId;
    private LocalDateTime generatedTime;
    private int totalAmount;
    private boolean paymentStatus;
    @ManyToOne
    @JoinColumn(name = "pay_id", referencedColumnName = "payId")
    private Payroll payroll;
}
