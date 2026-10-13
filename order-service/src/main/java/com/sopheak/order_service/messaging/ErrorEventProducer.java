
package com.sopheak.order_service.messaging;

import com.sopheak.order_service.event.ServiceErrorEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class ErrorEventProducer {

    private static final String TOPIC = "service-errors";

    private final KafkaTemplate<String, ServiceErrorEvent> kafkaTemplate;

    public ErrorEventProducer(
            KafkaTemplate<String, ServiceErrorEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishError(String errorMessage, String severity) {
        ServiceErrorEvent event = new ServiceErrorEvent(
                "order-service",
                errorMessage,
                severity,
                OffsetDateTime.now().toString()
        );

        kafkaTemplate.send(TOPIC, "order-service", event)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        System.err.println(
                                "Failed to publish service error event: "
                                        + exception.getMessage()
                        );
                    } else {
                        System.out.println(
                                "Error event published to Kafka topic: " + TOPIC
                        );
                    }
                });
    }
}
