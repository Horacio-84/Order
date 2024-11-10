package com.example.order.utils;

import com.example.order.model.Order;
import com.example.order.model.OrderItem;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderDeserializer extends JsonDeserializer<Order> {

    @Override
    public Order deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        Order order = new Order();
        order.setOrderId(node.get("orderId").asText());
        order.setTotalAmount(BigDecimal.valueOf(node.get("totalAmount").asDouble()));
        order.setStatus(node.get("status").asText());

        List<OrderItem> items = new ArrayList<>();
        for (JsonNode itemNode : node.get("items")) {
            OrderItem item = new OrderItem();
            item.setProductId(itemNode.get("productId").asText());
            item.setName(itemNode.get("name").asText());
            item.setQuantity(itemNode.get("quantity").asInt());
            item.setPrice(BigDecimal.valueOf(itemNode.get("price").asDouble()));
            items.add(item);
        }
        order.setItems(items);

        return order;
    }
}