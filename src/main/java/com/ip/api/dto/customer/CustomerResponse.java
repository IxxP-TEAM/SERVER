package com.ip.api.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CustomerResponse {
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private LocalDate customerSdate;
    private String customerStatus;
    private String customerAddress;
    private String customerAdddetail;
    private String customerPersonName;
    private String customerPersonPhone;
    private String customerPersonEmail;
    private String registrationNumber;
    private String customerNote;
    private String userName; // User의 이름만 포함
}
