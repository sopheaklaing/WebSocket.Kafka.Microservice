package com.sopheak.notification_service.messaging;

import com.sopheak.notification_service.event.OrderCreatedEvent;
import com.sopheak.notification_service.service.NotificationPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private final NotificationPublisher notificationPublisher;

    public OrderEventConsumer(
            NotificationPublisher notificationPublisher
    ) {
        this.notificationPublisher = notificationPublisher;
    }

    @KafkaListener(
            topics = "orders",
            groupId = "notification-service"
    )
    public void consumeOrderCreated(OrderCreatedEvent event) {

        System.out.println(
                "Received order event: " + event.getOrderId()
        );

        notificationPublisher.publishOrderCreated(event);
    }
}