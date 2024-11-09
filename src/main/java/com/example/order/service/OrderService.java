package com.example.order.service;

import com.example.order.model.Order;
import com.example.order.model.OrderItem;
import com.example.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order processOrder(String orderId, List<OrderItem> items) {

        // Verifica se o pedido já existe
        Optional<Order> existingOrder = orderRepository.findByOrderId(orderId);
        if (existingOrder.isPresent()) {
            return existingOrder.get();
        }

        // Calcula o valor total do pedido
        BigDecimal totalAmount = items.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Cria um novo pedido
        Order order = new Order();
        order.setOrderId(orderId);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");

        return orderRepository.save(order);
    }
}
