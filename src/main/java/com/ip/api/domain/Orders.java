package com.ip.api.domain;

import com.ip.api.domain.common.BaseEntity;
import com.ip.api.domain.enums.OrderStatus;
import com.ip.api.domain.enums.PaymentMethod;
import com.ip.api.domain.enums.PaymentStatus;
import com.ip.api.domain.enums.ShippingStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orders extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
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
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;
    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customerId")
    private Customer customer;
}
