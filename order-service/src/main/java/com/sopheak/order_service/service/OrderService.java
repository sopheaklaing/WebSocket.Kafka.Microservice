package com.sopheak.order_service.service;

import com.sopheak.order_service.entity.Order;
import com.sopheak.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(
            String customerName,
            String productName,
            Integer quantity
    ) {
        Order order = new Order(
                customerName,
                productName,
                quantity
        );

        return orderRepository.save(order);
    }
}