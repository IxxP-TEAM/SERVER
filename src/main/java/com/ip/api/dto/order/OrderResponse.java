package com.ip.api.dto.order;

import com.ip.api.domain.Orders;
import com.ip.api.domain.enums.OrderStatus;
import com.ip.api.domain.enums.PaymentMethod;
import com.ip.api.domain.enums.PaymentStatus;
import com.ip.api.domain.enums.ShippingStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class OrderResponse {
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
    private Long userId;
    private Long customerId;

    public OrderResponse(Orders order) {
        this.orderId = order.getOrderId();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getOrderStatus();
        this.totalAmount = order.getTotalAmount();
        this.paymentMethod = order.getPaymentMethod();
        this.paymentStatus = order.getPaymentStatus();
        this.shippingAddr = order.getShippingAddr();
        this.shippingSdate = order.getShippingSdate();
        this.shippingStatus = order.getShippingStatus();
        this.discountAmount = order.getDiscountAmount();
        this.taxAmount = order.getTaxAmount();
        this.orderNote = order.getOrderNote();
        this.userId = order.getUser().getUserId();
        this.customerId = order.getCustomer().getCustomerId();
    }
}
