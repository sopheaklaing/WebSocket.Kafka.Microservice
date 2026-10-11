
package com.sopheak.alert_service.controller;

import com.sopheak.alert_service.event.ServiceErrorEvent;
import com.sopheak.alert_service.service.ErrorEventProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alerts")
public class AlertTestController {

    private final ErrorEventProducer errorEventProducer;

    public AlertTestController(ErrorEventProducer errorEventProducer) {
        this.errorEventProducer = errorEventProducer;
    }

    @PostMapping("/test")
    public ResponseEntity<String> sendTestAlert(
            @RequestBody ServiceErrorEvent event) {

        errorEventProducer.publish(event);

        return ResponseEntity.accepted()
                .body("Test alert event submitted to Kafka");
    }
}
