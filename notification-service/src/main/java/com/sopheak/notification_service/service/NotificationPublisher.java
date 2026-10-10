package com.sopheak.notification_service.service;

import com.sopheak.notification_service.event.OrderCreatedEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationPublisher(
            SimpMessagingTemplate messagingTemplate
    ) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {
        messagingTemplate.convertAndSend(
                "/topic/orders",
                event
        );
    }
}