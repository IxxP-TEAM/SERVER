package com.ip.api.dto.customer;


import com.ip.api.domain.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


public class CustomerRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerDTO{
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
        private String registrationNumber;
        private String customerNote;

        private Long userId;
    }
}