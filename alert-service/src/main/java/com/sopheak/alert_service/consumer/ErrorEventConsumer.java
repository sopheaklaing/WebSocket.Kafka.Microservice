package com.sopheak.alert_service.consumer;

import com.sopheak.alert_service.event.ServiceErrorEvent;
import com.sopheak.alert_service.service.TelegramService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ErrorEventConsumer {

    private final TelegramService telegramService;

    public ErrorEventConsumer(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @KafkaListener(
        topics = "service-errors",
        groupId = "alert-service",
        properties = {
                "spring.json.value.default.type=com.sopheak.alert_service.event.ServiceErrorEvent",
                "spring.json.use.type.headers=false"
        }
)
    public void consumeError(ServiceErrorEvent event) {

        String message = """
                🚨 MICROSERVICE ERROR ALERT

                Service: %s
                Severity: %s
                Error: %s
                Time: %s
                """.formatted(
                event.getServiceName(),
                event.getSeverity(),
                event.getErrorMessage(),
                event.getTimestamp()
        );

        telegramService.sendMessage(message);

        System.out.println(
                "Telegram alert sent for service: "
                        + event.getServiceName()
        );
    }
}