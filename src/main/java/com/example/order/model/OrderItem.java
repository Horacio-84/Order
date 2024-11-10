package com.example.order.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItem {

    private String productId;
    private String name;
    private Integer quantity;
    private BigDecimal price;
}
