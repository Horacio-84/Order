package com.example.order.orderService.consumer;

import com.example.order.orderService.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class OrderConsumer {

    private final OrderService orderService;

    public OrderConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = "order.queue")
    public void receiveOrder(Map<String, Object> orderData) {
        String orderId = (String) orderData.get("orderId");
        BigDecimal totalAmount = new BigDecimal(orderData.get("totalAmount").toString());

        // Processa o pedido para evitar duplicação
        orderService.processOrder(orderId, totalAmount);
    }
}
