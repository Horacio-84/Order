package com.example.order.service;

import com.example.order.model.Order;
import com.example.order.model.OrderItem;
import com.example.order.model.enums.Status;
import com.example.order.repository.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static com.example.order.config.RabbitMQConfig.ORDER_QUEUE;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    public OrderService(OrderRepository orderRepository, RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
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

        List<Order> orders = orderRepository.findOrdersByStatus(Status.PENDING.toString(), Limit.of(10000));
        orders.forEach(order -> {
            // Calcular o totalAmount
            order.setTotalAmount(order.getItems().stream()
                    .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add));

            // Definir o status como COMPLETED
            order.setStatus(Status.COMPLETED.toString());
        });

        orderRepository.saveAll(orders);
    }

    public void mountAndSendOrdersToQueue(String orderId, List<OrderItem> items) {
        // Monta os dados do pedido a serem enviados para a fila
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderId", orderId);
        // Converte a lista de OrderItem para um formato adequado para a fila
        if (!Objects.isNull(items) && !items.isEmpty()) {
            List<Map<String, Object>> itemsData = items.stream().map(item -> {
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("productId", item.getProductId());
                itemData.put("name", item.getName());
                itemData.put("quantity", item.getQuantity());
                itemData.put("price", item.getPrice());
                return itemData;
            }).toList();

            orderData.put("items", itemsData);
        }

        // Envia os dados para a fila
        rabbitTemplate.convertAndSend(ORDER_QUEUE, orderData);
    }

    public Order findById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado!"));
    }

    @Transactional
    public List<Order> findOrdersByStatus(Status status) {
        List<Order> completedOrders = orderRepository.findOrdersByStatus(status.toString(), Limit.of(10000));

        updateStatus(completedOrders, Status.TRANSMITTED);

        return completedOrders;
    }

    private void updateStatus(List<Order> orders, Status status) {

        orders.forEach(order -> order.setStatus(status.toString()));
        orderRepository.saveAll(orders);
    }
}