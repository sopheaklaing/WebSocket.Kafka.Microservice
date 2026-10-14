
package com.sopheak.alert_service.consumer;

import com.sopheak.alert_service.event.OrderCreatedEvent;
import com.sopheak.alert_service.service.TelegramService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private final TelegramService telegramService;

    public OrderEventConsumer(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

   @KafkaListener(
        topics = "orders",
        groupId = "alert-service-orders",
        properties = {
                "spring.json.value.default.type=com.sopheak.alert_service.event.OrderCreatedEvent",
                "spring.json.use.type.headers=false"
        }
)
    public void consumeOrderCreated(OrderCreatedEvent event) {

        String message = """
                🛒 NEW ORDER CREATED

                Order ID: %s
                Customer: %s
                Product: %s
                Quantity: %s
                Status: CREATED
                """.formatted(
                event.getOrderId(),
                event.getCustomerName(),
                event.getProductName(),
                event.getQuantity()
        );

        telegramService.sendMessage(message);

        System.out.println(
                "Telegram order alert sent for order: "
                        + event.getOrderId()
        );
    }
}