package com.ip.api.domain;

import com.ip.api.domain.common.BaseEntity;
import com.ip.api.domain.enums.Status;
import jakarta.persistence.*;

import java.time.LocalDate;

import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private LocalDate customerSdate;
    private Status customerStatus;
    private String customerAddress;
    private String customerAdddetail;
    private String customerPersonName;
    private String customerPersonPhone;
    private String customerPersonEmail;
    @Column(unique = true)
    private String registrationNumber;
    private String customerNote;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;
}
