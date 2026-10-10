package com.sopheak.notification_service.event;

public class OrderCreatedEvent {

    private Long orderId;
    private String customerName;
    private String productName;
    private Integer quantity;

    public OrderCreatedEvent() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }
}