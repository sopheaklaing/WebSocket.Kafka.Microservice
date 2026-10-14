
package com.sopheak.alert_service.consumer;

import com.sopheak.alert_service.event.ProductEvent;
import com.sopheak.alert_service.service.TelegramService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductEventConsumer {

    private final TelegramService telegramService;

    public ProductEventConsumer(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @KafkaListener(
            topics = "product-events",
            groupId = "alert-service-products",
            properties = {
                    "spring.json.value.default.type=com.sopheak.alert_service.event.ProductEvent",
                    "spring.json.use.type.headers=false"
            }
    )
    public void consumeProductEvent(ProductEvent event) {

        if (!"PRODUCT_CREATED".equals(event.getEventType())) {
            return;
        }

        String message = """
                📦 NEW PRODUCT CREATED

                Product ID: %s
                Name: %s
                Price: $%s
                Stock: %s
                Event: %s
                Time: %s
                """.formatted(
                event.getProductId(),
                event.getName(),
                event.getPrice(),
                event.getStock(),
                event.getEventType(),
                event.getTimestamp()
        );

        telegramService.sendMessage(message);

        System.out.println(
                "Telegram product alert sent for product: "
                        + event.getProductId()
        );
    }
}