
package com.sopheak.alert_service.service;

import com.sopheak.alert_service.event.ServiceErrorEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ErrorEventProducer {

    private static final String TOPIC = "service-errors";

    private final KafkaTemplate<String, ServiceErrorEvent> kafkaTemplate;

    public ErrorEventProducer(
            KafkaTemplate<String, ServiceErrorEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ServiceErrorEvent event) {
        kafkaTemplate.send(TOPIC, event.getServiceName(), event);
    }
}
