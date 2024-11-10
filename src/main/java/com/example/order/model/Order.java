package com.example.order.model;

import com.example.order.utils.OrderDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.ArrayBlockingQueueDeserializer;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "orders")
public class Order {

    @Id
    private String id;
    @Indexed(unique = true)
    private String orderId;
    private BigDecimal totalAmount;
    private String status;
    private List<OrderItem> items;

}
