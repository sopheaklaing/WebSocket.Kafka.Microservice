package com.sopheak.order_service.service;

import com.sopheak.order_service.entity.Order;
import com.sopheak.order_service.repository.OrderRepository;
import com.sopheak.order_service.messaging.OrderEventProducer;
import com.sopheak.order_service.event.OrderCreatedEvent;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    // this object is used to interact with the database for order-related
    // operations
    // this is for OrderService → Database
    private final OrderRepository orderRepository;
    // this object is used to publish order-related events to a messaging system
    // (like Kafka)
    // this is for OrderService → Messaging
    private final OrderEventProducer orderEventProducer;

    // ========================
    // Constructor Injection
    // ========================
    // Constructor injection is a way to provide dependencies to a class through its
    // constructor.
    // In this case, the OrderService class requires two dependencies:
    // OrderRepository and OrderEvent
    public OrderService(
            OrderRepository orderRepository,
            OrderEventProducer orderEventProducer

    ) {
        this.orderRepository = orderRepository;
        this.orderEventProducer = orderEventProducer;
    }

    // this is method to create a new order, save it to the database, and publish an
    // event indicating that the order has been created
    // this is for OrderService → Database → Messaging
    public Order createOrder(
            String customerName,
            String productName,
            Integer quantity) {
        // Create Order Object
        // Order
        // -----------------
        // customerName = Sopheak
        // productName = Laptop
        // quantity = 1
        Order order = new Order(
                customerName,
                productName,
                quantity);
        //  Save to Database
        Order savedOrder = orderRepository.save(order);
        // Create Kafka Event
        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getCustomerName(),
                savedOrder.getProductName(),
                savedOrder.getQuantity());

        orderEventProducer.publishOrderCreated(event);

        return savedOrder;
    }
}