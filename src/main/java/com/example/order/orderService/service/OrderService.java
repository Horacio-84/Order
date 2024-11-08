package com.example.order.orderService.service;

import com.example.order.orderService.entity.Order;
import com.example.order.orderService.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order processOrder(String orderId, BigDecimal totalAmount) {

        // Verifica se o pedido já existe
        Optional<Order> existingOrder = orderRepository.findByOrderId(orderId);
        if (existingOrder.isPresent()) {
            return existingOrder.get();
        }

        // Cria um novo pedido
        Order order = new Order();
        order.setOrderId(orderId);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");

        return orderRepository.save(order);
    }
}
