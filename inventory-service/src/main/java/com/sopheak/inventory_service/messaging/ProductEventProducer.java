
package com.sopheak.inventory_service.messaging;

import com.sopheak.inventory_service.event.ProductEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductEventProducer {

    private static final String TOPIC = "product-events";

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    public ProductEventProducer(
            KafkaTemplate<String, ProductEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ProductEvent event) {
        kafkaTemplate.send(
                TOPIC,
                event.getProductId().toString(),
                event
        );
    }
}