package com.example.order.controller;

import com.example.order.model.Order;
import com.example.order.model.OrderItem;
import com.example.order.repository.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.order.config.RabbitMQConfig.ORDER_QUEUE;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    public OrderController(OrderRepository orderRepository, RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado!"));
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> createOrder(
            @PathVariable String id,
            @RequestBody List<OrderItem> items) {

        mountAndSendOrdersToQueue(id, items);

        return new ResponseEntity<>("Order sent to queue successfully!", HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<String> createOrders(@RequestBody List<Order> orders) {

        for (Order order : orders) {
            mountAndSendOrdersToQueue(order.getId(), order.getItems());
        }

        return new ResponseEntity<>("Orders sent to queue successfully!", HttpStatus.CREATED);
    }

    private void mountAndSendOrdersToQueue(String orderId, List<OrderItem> items) {
        // Monta os dados do pedido a serem enviados para a fila
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderId", orderId);
        // Converte a lista de OrderItem para um formato adequado para a fila
        List<Map<String, Object>> itemsData = items.stream().map(item -> {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("productId", item.getProductId());
            itemData.put("name", item.getName());
            itemData.put("quantity", item.getQuantity());
            itemData.put("price", item.getPrice());
            return itemData;
        }).toList();

        orderData.put("items", itemsData);

        // Envia os dados para a fila
        rabbitTemplate.convertAndSend(ORDER_QUEUE, orderData);
    }
}
