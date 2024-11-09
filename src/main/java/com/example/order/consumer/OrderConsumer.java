package com.example.order.consumer;

import com.example.order.model.OrderItem;
import com.example.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

        List<Map<String, Object>> itemsData = (List<Map<String, Object>>) orderData.get("items");
        List<OrderItem> items = new ArrayList<>();

        for (Map<String, Object> itemData : itemsData) {
            OrderItem item = new OrderItem();
            item.setProductId((String) itemData.get("productId"));
            item.setName((String) itemData.get("name"));
            item.setQuantity((Integer) itemData.get("quantity"));
            item.setPrice(new BigDecimal(itemData.get("price").toString()));
            items.add(item);
        }

        // Processa o pedido com os itens
        orderService.processOrder(orderId, items);
    }
}
