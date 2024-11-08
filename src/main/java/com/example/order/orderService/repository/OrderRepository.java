package com.example.order.orderService.repository;

import com.example.order.orderService.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    Optional<Order> findByOrderId(String orderId);
}
