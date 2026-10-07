# WebSocket.Kafka.Microservice
# Real-Time Order & Notification System

A microservices-based backend project built with **Java 21, Spring Boot, PostgreSQL, Docker, Kafka, and WebSocket**.

## Services

* `order-service` — Order management
* `inventory-service` — Inventory management
* `notification-service` — Real-time notifications
* `alert-service` — Error alerts via Telegram

## Tech Stack

* Java 21
* Spring Boot
* PostgreSQL
* Apache Kafka
* WebSocket
* Docker & Docker Compose
* Redis
* Telegram Bot

## Architecture

```text
Client
  ↓
Order Service
  ↓
Kafka
  ├──→ Inventory Service
  └──→ Notification Service
            ↓
         WebSocket
            ↓
          Client

Errors
  ↓
Alert Service
  ↓
Telegram
```

> This project is built step by step to learn **Microservices, Event-Driven Architecture, Kafka, WebSocket, and Real-Time Communication**.
