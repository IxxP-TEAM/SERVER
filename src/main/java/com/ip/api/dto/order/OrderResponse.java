package com.ip.api.dto.order;

import com.ip.api.domain.OrderProduct;
import com.ip.api.domain.Orders;
import com.ip.api.repository.OrdersRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private LocalDate orderDate;
    private String orderStatus;
    private int totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String shippingAddr;
    private String shippingSdate;
    private String shippingStatus;
    private int discountAmount;
    private int taxAmount;
    private String orderNote;
    private Long userId;
    private Long customerId;
    private List<OrderProductResponse> products;

    public OrderResponse(Orders order) {
        this.orderId = order.getOrderId();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getOrderStatus() != null ? order.getOrderStatus().toString() : null;
        this.totalAmount = order.getTotalAmount();
        this.paymentMethod = order.getPaymentMethod() != null ? order.getPaymentMethod().toString() : null;
        this.paymentStatus = order.getPaymentStatus() != null ? order.getPaymentStatus().toString() : null;
        this.shippingAddr = order.getShippingAddr();
        this.shippingSdate = order.getShippingSdate();
        this.shippingStatus = order.getShippingStatus() != null ? order.getShippingStatus().toString() : null;
        this.discountAmount = order.getDiscountAmount();
        this.taxAmount = order.getTaxAmount();
        this.orderNote = order.getOrderNote();
        this.userId = order.getUser().getUserId();
        this.customerId = order.getCustomer().getCustomerId();

        // null 체크 추가
        this.products = order.getOrderProducts() != null
                ? order.getOrderProducts().stream().map(OrderProductResponse::new).collect(Collectors.toList())
                : new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderProductResponse {
        private Long productId;
        private String productName;
        private Long quantity;
        private BigDecimal price;
        private BigDecimal subtotal;
        private BigDecimal discount;
        private BigDecimal tax;

        public OrderProductResponse(OrderProduct orderProduct) {
            this.productId = orderProduct.getProduct().getProductId();
            this.productName = orderProduct.getProduct().getProductName();
            this.quantity = orderProduct.getQuantity();
            //this.price = orderProduct.getPrice();
            this.subtotal = orderProduct.getSubtotal();
            this.discount = orderProduct.getDiscount();
            this.tax = orderProduct.getTax();
        }
    }

}
