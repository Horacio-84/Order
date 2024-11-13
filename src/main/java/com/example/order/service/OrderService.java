package com.example.order.service;

import com.example.order.model.Order;
import com.example.order.model.OrderItem;
import com.example.order.model.enums.Status;
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

        System.out.println("Entering order: " + orderId);
        // Verifica se o pedido já existe
        Optional<Order> existingOrder = orderRepository.findByOrderId(orderId);
        if (existingOrder.isPresent()) {
            return existingOrder.get();
        }

        // Cria um novo pedido
        Order order = new Order();
        order.setOrderId(orderId);
        order.setItems(items);
        order.setStatus(Status.PENDING.toString());

        System.out.println("saving order: " + order);

        return orderRepository.save(order);
    }

    @Transactional
    public void calculateTotal() {

        List<Order> orders = orderRepository.findOrdersByStatus(Status.PENDING.toString());
        for (Order order : orders) {
            order.setTotalAmount(order.getItems().stream()
                    .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add));

            order.setStatus(Status.COMPLETED.toString());
        }

        orderRepository.saveAll(orders);
    }
}
