package com.sopheak.inventory_service.messaging;

import com.sopheak.inventory_service.event.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    @KafkaListener(
            topics = "orders",
            groupId = "inventory-service"
    )
    public void consumeOrderCreated(OrderCreatedEvent event) {

        System.out.println(
                "Received order: " + event.getOrderId()
                        + " | Product: " + event.getProductName()
                        + " | Quantity: " + event.getQuantity()
        );
    }
}