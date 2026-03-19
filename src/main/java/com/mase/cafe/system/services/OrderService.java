package com.mase.cafe.system.services;

import com.mase.cafe.system.dtos.OrderDTO;
import com.mase.cafe.system.models.Order;
import com.mase.cafe.system.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(Order order) {

        if (order.getOrderItems() != null) {
            order.getOrderItems().forEach(item -> item.setOrder(order));
        }

        return orderRepository.save(order);
    }

    public OrderDTO getOrder(LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findByOrderTimestampBetween(start, end);

        double revenue = orders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();

        return new OrderDTO(orders, revenue, orders.size());
    }
}