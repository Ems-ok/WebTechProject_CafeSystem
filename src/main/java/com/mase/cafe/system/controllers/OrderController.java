package com.mase.cafe.system.controllers;

import com.mase.cafe.system.dtos.OrderDTO;
import com.mase.cafe.system.models.Order;
import com.mase.cafe.system.models.User;
import com.mase.cafe.system.repositories.OrderRepository;
import com.mase.cafe.system.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(401).body("Error: Logged-in user not found.");
        }

        order.setUser(user);

        if (order.getOrderItems() != null) {
            order.getOrderItems().forEach(item -> item.setOrder(order));
        }

        try {
            Order savedOrder = orderRepository.save(order);
            return ResponseEntity.status(201).body(savedOrder);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Backend Error: " + e.getMessage());
        }
    }

    @GetMapping
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream().map(order -> {
            OrderDTO dto = new OrderDTO();
            dto.setId(order.getId());
            dto.setOrdername(order.getOrdername());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setOrderTimestamp(order.getOrderTimestamp());
            if (order.getUser() != null) {
                dto.setUsername(order.getUser().getUsername());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<Order> getOrdersByTime(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        return orderRepository.findByOrderTimestampBetween(start, end);
    }

}