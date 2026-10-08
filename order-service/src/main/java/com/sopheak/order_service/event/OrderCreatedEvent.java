package com.sopheak.order_service.event;

public class OrderCreatedEvent {
    private Long orderId;
    private String customerName;
    private String productName;
    private Integer quantity;

    public OrderCreatedEvent(
        Long orderId, 
        String customerName, 
        String productName, 
        Integer quantity
    ) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.productName = productName;
        this.quantity = quantity;
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
