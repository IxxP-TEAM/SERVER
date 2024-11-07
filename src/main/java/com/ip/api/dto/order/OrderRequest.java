package com.ip.api.dto.order;

import com.ip.api.domain.Orders;
import com.ip.api.domain.enums.OrderStatus;
import com.ip.api.domain.enums.PaymentMethod;
import com.ip.api.domain.enums.PaymentStatus;
import com.ip.api.domain.enums.ShippingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class OrderRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDTO { // Corrected: Changed "void" to "class"
        private LocalDate orderDate;
        private OrderStatus orderStatus;
        private int totalAmount;
        private PaymentMethod paymentMethod;
        private PaymentStatus paymentStatus;
        private String shippingAddr;
        private String shippingSdate;
        private ShippingStatus shippingStatus;
        private int discountAmount;
        private int taxAmount;
        private String orderNote;
        private Long userId; // User ID reference
        private Long customerId; // Customer ID reference

        // Converts the DTO to an Orders entity.
        // Note: This method does not set user or customer, which should be set in the Service layer.
        public Orders toEntity() {
            return Orders.builder()
                    .orderDate(orderDate)
                    .orderStatus(orderStatus)
                    .totalAmount(totalAmount)
                    .paymentMethod(paymentMethod)
                    .paymentStatus(paymentStatus)
                    .shippingAddr(shippingAddr)
                    .shippingSdate(shippingSdate)
                    .shippingStatus(shippingStatus)
                    .discountAmount(discountAmount)
                    .taxAmount(taxAmount)
                    .orderNote(orderNote)
                    .build();
        }
    }
}
