package com.sopheak.order_service.controller;

import com.sopheak.order_service.entity.Order;
import com.sopheak.order_service.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sopheak.order_service.dto.OrderRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @Valid @RequestBody OrderRequest request
    ) {
        Order order = orderService.createOrder(
                request.getCustomerName(),
                request.getProductName(),
                request.getQuantity()
        );

        return ResponseEntity.ok(order);
    }
}