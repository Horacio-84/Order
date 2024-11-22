package com.example.order.controller;

import com.example.order.model.Order;
import com.example.order.model.enums.Status;
import com.example.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/completed")
    public List<Order> getCompletedOrders() {

        return orderService.findOrdersByStatus(Status.COMPLETED);
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable String id) {
        return orderService.findById(id);
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> createOrder(
            @PathVariable String id,
            @RequestBody Order order) {

        orderService.mountAndSendOrdersToQueue(id, order.getItems());

        return new ResponseEntity<>("Order sent to queue successfully!", HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<String> createOrders(@RequestBody List<Order> orders) {

        for (Order order : orders) {
            orderService.mountAndSendOrdersToQueue(order.getOrderId(), order.getItems());
        }

        return new ResponseEntity<>("Orders sent to queue successfully!", HttpStatus.CREATED);
    }
}
