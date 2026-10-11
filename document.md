# WebSocket + Kafka Microservices

A Spring Boot microservices project using Apache Kafka for asynchronous event communication and WebSocket with STOMP for real-time notifications.

## 1. Project Overview

The system allows clients to create orders through a REST API. The Order Service saves orders in PostgreSQL and publishes order-created events to Kafka. Inventory Service and Notification Service independently consume these events.

Notification Service then broadcasts order notifications to connected browser clients through WebSocket.

### Technologies

- Java 21
- Spring Boot 4.1.1
- Apache Kafka
- Spring Kafka
- WebSocket and STOMP
- PostgreSQL 17
- Maven
- Docker and Docker Compose
- Postman

## 2. Microservices

| Service | Port | Responsibility |
|---|---:|---|
| Order Service | 8081 | REST API, save orders, publish events |
| Inventory Service | 8082 | Consume order events and process inventory-related logic |
| Notification Service | 8083 | Consume events and broadcast WebSocket notifications |
| Kafka | 9092 | Event messaging between services |
| PostgreSQL | 5433 or configured internal port | Persist order data |

The Alert Service and Telegram Bot integration are planned future improvements.

## 3. Architecture

```text
                  Client / Postman
                         |
                         v
                  Order Service
                    Port 8081
                         |
                  Save Order
                         |
                         v
                     PostgreSQL
                         |
                         v
                 OrderEventProducer
                         |
                         v
                  Kafka: orders
                    /          \
                   v            v
          Inventory Service   Notification Service
              Port 8082          Port 8083
                   |                 |
             Process Event     OrderEventConsumer
                                     |
                                     v
                            NotificationPublisher
                                     |
                                     v
                              WebSocket + STOMP
                               Endpoint: /ws
                                     |
                                     v
                              Browser Client
                           Subscribe: /topic/orders
```

## 4. Order Service — Kafka Producer

### 4.1 Add Kafka Dependency

File: `order-service/pom.xml`

Add inside the `<dependencies>` section:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-kafka</artifactId>
</dependency>
```

### 4.2 Create OrderCreatedEvent

File: `order-service/src/main/java/com/sopheak/order_service/event/OrderCreatedEvent.java`

```java
package com.sopheak.order_service.event;

public class OrderCreatedEvent {

    private Long orderId;
    private String customerName;
    private String productName;
    private Integer quantity;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(
            Long orderId,
            String customerName,
            String productName,
            Integer quantity
    ) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.productName = productName;
        this.quantity = quantity;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
```

Purpose: Represents the event published when an order is created.

### 4.3 Create OrderEventProducer

File: `order-service/src/main/java/com/sopheak/order_service/messaging/OrderEventProducer.java`

```java
package com.sopheak.order_service.messaging;

import com.sopheak.order_service.event.OrderCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {

    private static final String TOPIC = "orders";

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderEventProducer(
            KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send(
                TOPIC,
                event.getOrderId().toString(),
                event
        );
    }
}
```

How it works:

- `KafkaTemplate` sends events to Kafka.
- `orders` is the destination topic.
- The order ID is used as the Kafka message key.
- `OrderCreatedEvent` is serialized as JSON.

### 4.4 Configure the Kafka Producer

File: `order-service/src/main/resources/application.properties`

Add:

```properties
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
```

The key serializer converts the order ID key to a Kafka-compatible string representation. The JSON serializer converts the event object into JSON bytes.

### 4.5 Publish the Event from OrderService

After saving an order, publish an event using the saved entity:

```java
Order savedOrder = orderRepository.save(order);

OrderCreatedEvent event = new OrderCreatedEvent(
        savedOrder.getId(),
        savedOrder.getCustomerName(),
        savedOrder.getProductName(),
        savedOrder.getQuantity()
);

orderEventProducer.publishOrderCreated(event);

return savedOrder;
```

Inject `OrderEventProducer` into `OrderService` through its constructor.

Important: This is the event-publishing logic to integrate into your existing service method, not a replacement for your entire service class.

## 5. Inventory Service — Kafka Consumer

Inventory Service independently consumes events from the `orders` topic.

Its event class should match the JSON structure published by Order Service:

```java
package com.sopheak.inventory_service.event;

public class OrderCreatedEvent {

    private Long orderId;
    private String customerName;
    private String productName;
    private Integer quantity;

    public OrderCreatedEvent() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
```

The consumer listens for order events:

```java
@KafkaListener(topics = "orders", groupId = "inventory-service")
public void consumeOrderCreated(OrderCreatedEvent event) {
    System.out.println(
        "Received order: " + event.getOrderId()
        + " | Product: " + event.getProductName()
        + " | Quantity: " + event.getQuantity()
    );
}
```

Ensure the class containing this method is a Spring-managed component and the consumer JSON deserializer is configured correctly.

## 6. Notification Service — Kafka + WebSocket

Notification Service has two responsibilities:

1. Consume order-created events from Kafka.
2. Broadcast the events to WebSocket clients.

### 6.1 Add Dependencies

File: `notification-service/pom.xml`

The relevant dependencies are:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-kafka</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>

<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

The Jackson dependency supplies JSON processing classes needed by the Kafka JSON deserializer.

Keep the existing Spring Boot parent version, Java version, build plugin, and test dependencies.

### 6.2 Create the Event Class

File: `notification-service/src/main/java/com/sopheak/notification_service/event/OrderCreatedEvent.java`

```java
package com.sopheak.notification_service.event;

public class OrderCreatedEvent {

    private Long orderId;
    private String customerName;
    private String productName;
    private Integer quantity;

    public OrderCreatedEvent() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
```

This class maps the JSON payload consumed from Kafka. The field names and types must match the published event.

### 6.3 Configure WebSocket

File: `notification-service/src/main/java/com/sopheak/notification_service/config/WebSocketConfig.java`

```java
package com.sopheak.notification_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig
        implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }
}
```

Configuration details:

- `@EnableWebSocketMessageBroker`: Enables STOMP messaging over WebSocket.
- `/ws`: The WebSocket connection endpoint.
- `/topic`: Prefix for destinations clients can subscribe to.
- `/app`: Prefix for client messages routed to application handlers.
- `enableSimpleBroker("/topic")`: Enables Spring's in-memory message broker.

For production, restrict allowed origins to trusted client origins rather than using `"*"`.

### 6.4 Create NotificationPublisher

File: `notification-service/src/main/java/com/sopheak/notification_service/service/NotificationPublisher.java`

```java
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
        messagingTemplate.convertAndSend("/topic/orders", event);
    }
}
```

`SimpMessagingTemplate` sends the event to all currently subscribed clients at `/topic/orders`.

### 6.5 Create OrderEventConsumer

File: `notification-service/src/main/java/com/sopheak/notification_service/messaging/OrderEventConsumer.java`

```java
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
```

Flow:

1. Kafka delivers an order event.
2. The consumer deserializes it into `OrderCreatedEvent`.
3. The consumer calls `NotificationPublisher`.
4. The publisher broadcasts the event to `/topic/orders`.
5. Connected clients subscribed to that destination receive the JSON message.

### 6.6 Configure Notification Service

File: `notification-service/src/main/resources/application.properties`

```properties
spring.application.name=notification-service
server.port=8083

spring.kafka.bootstrap-servers=${SPRING_KAFKA_BOOTSTRAP_SERVERS:kafka:9092}
spring.kafka.consumer.group-id=notification-service
spring.kafka.consumer.auto-offset-reset=earliest

spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer

spring.kafka.consumer.properties.spring.json.trusted.packages=com.sopheak.notification_service.event
spring.kafka.consumer.properties.spring.json.value.default.type=com.sopheak.notification_service.event.OrderCreatedEvent
spring.kafka.consumer.properties.spring.json.use.type.headers=false
```

In Docker Compose, the bootstrap server should use the Kafka service name, such as `kafka:9092`, provided that this is the broker's internal listener address.

## 7. WebSocket Browser Client

Create a file named `websocket-test.html` to test real-time order notifications.

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Order Notifications</title>
</head>
<body>
  <h2>Order Notifications</h2>
  <p id="status">Connecting...</p>
  <pre id="messages"></pre>

  <script src="https://cdn.jsdelivr.net/npm/@stomp/stompjs@7/bundles/stomp.umd.min.js"></script>

  <script>
    const status = document.getElementById("status");
    const messages = document.getElementById("messages");

    const client = new StompJs.Client({
      brokerURL: "ws://localhost:8083/ws",
      reconnectDelay: 5000,

      onConnect: () => {
        status.textContent =
          "Connected! Subscribed to /topic/orders";

        client.subscribe("/topic/orders", (message) => {
          const order = JSON.parse(message.body);

          messages.textContent +=
            JSON.stringify(order, null, 2) + "\n\n";
        });
      },

      onStompError: (frame) => {
        status.textContent =
          "STOMP error: " + frame.headers.message;
      },

      onWebSocketError: () => {
        status.textContent = "WebSocket connection error";
      }
    });

    client.activate();
  </script>
</body>
</html>
```

Open the file in a browser while Notification Service is running. The browser should show:

```text
Connected! Subscribed to /topic/orders
```

The STOMP client handles the STOMP frames and subscription protocol.

## 8. Kafka Message Format

Example event value:

```json
{
  "orderId": 12,
  "customerName": "Sopheak",
  "productName": "Keyboard",
  "quantity": 2
}
```

| Property | Value |
|---|---|
| Topic | `orders` |
| Message key | `"12"` |
| Message value | JSON representation of `OrderCreatedEvent` |

Kafka serializes the message key and value into bytes. Consumers deserialize the bytes into their own event classes.

Using the order ID as the key can route events with the same key to the same partition under a consistent partitioning configuration.

## 9. Run with Docker Compose

Run the commands from the project root directory containing `compose.yaml` or `docker-compose.yml`.

Build and start the services:

```powershell
docker compose up -d --build
```

Check running containers:

```powershell
docker compose ps
```

Inspect Notification Service logs:

```powershell
docker compose logs --tail=100 notification-service
```

Expected logs include messages indicating that the application started, the Kafka consumer subscribed to `orders`, and the consumer received order events.

## 10. Test the Complete Flow

1. Start Kafka, PostgreSQL, and the application services.
2. Open `websocket-test.html` in your browser.
3. Confirm that the client is connected and subscribed.
4. In Postman, create a `POST` request to:

   `http://localhost:8081/api/orders`

5. Use `Body → raw → JSON` and send:

```json
{
  "customerName": "Sopheak",
  "productName": "Mouse",
  "quantity": 1
}
```

6. Confirm the API response contains the newly created order.
7. Check the browser for the new order JSON without refreshing the page.
8. Check the Inventory and Notification Service logs.

Example notification:

```json
{
  "customerName": "Sopheak",
  "orderId": 14,
  "productName": "Mouse",
  "quantity": 1
}
```

## 11. Kafka vs. WebSocket

| Feature | Kafka | WebSocket |
|---|---|---|
| Main purpose | Event communication between services | Real-time client-server communication |
| Connection | Producers and consumers use the broker | Persistent connection between client and server |
| In this project | Transports order events | Pushes order notifications to browsers |
| Example | Order Service → Notification Service | Notification Service → Browser |

Kafka and WebSocket are complementary technologies, not alternatives to each other.

## 12. Important Engineering Considerations

1. **Independent consumer groups:** Inventory and Notification Service use different group IDs so each service can independently consume the same topic.
2. **Error handling:** Configure retries and a dead-letter topic for messages that cannot be processed.
3. **Database consistency:** Saving an order and publishing an event are separate operations. Consider the transactional outbox pattern to reduce the risk of losing an event between those operations.
4. **Idempotency:** Consumers should handle duplicate events safely.
5. **WebSocket security:** Restrict allowed origins and apply appropriate authentication and authorization.
6. **Monitoring:** Track consumer lag, failures, service health, and notification delivery.
7. **Production delivery guarantees:** A successful Kafka publish does not prove that every consumer processed the event or that a browser received it.

## 13. Current Project Status

Completed and tested:

- Order Service saves orders and publishes Kafka events.
- Inventory Service consumes order events.
- Notification Service consumes order events.
- WebSocket endpoint `/ws` accepts connections.
- STOMP clients subscribe to `/topic/orders`.
- Browser receives order notifications in real time.
- Docker Compose runs the services.

Next planned improvements:

- Alert Service for application error events.
- Telegram Bot integration for error alerts.
- Observability with metrics, logs, and dashboards.
- DevSecOps security scans in CI/CD.
- More reliable event publishing and consumer error handling.

**Project result:** A working event-driven microservices flow using REST, PostgreSQL, Kafka, Spring Boot, WebSocket, STOMP, and Docker Compose.