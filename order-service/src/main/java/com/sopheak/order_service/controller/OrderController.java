package com.sopheak.order_service.controller;

import com.sopheak.order_service.entity.Order;
import com.sopheak.order_service.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestParam String customerName,
            @RequestParam String productName,
            @RequestParam Integer quantity
    ) {
        Order order = orderService.createOrder(
                customerName,
                productName,
                quantity
        );

        return ResponseEntity.ok(order);
    }
}