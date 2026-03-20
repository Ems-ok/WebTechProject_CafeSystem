package com.mase.cafe.system.controllers;

import com.mase.cafe.system.dtos.OrderDTO;
import com.mase.cafe.system.dtos.TopSellingItemDTO;
import com.mase.cafe.system.models.Order;
import com.mase.cafe.system.repositories.OrderRepository;
import com.mase.cafe.system.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order, Principal principal) {
        try {
            Order savedOrder = orderService.saveOrder(order, principal.getName());
            return ResponseEntity.status(201).body(orderService.convertToDTO(savedOrder));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping
    public List<OrderDTO> getAllOrders() {
        // Uses service to get clean DTO list
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(order -> ResponseEntity.ok(orderService.convertToDTO(order)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<OrderDTO> getOrdersByTime(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        return orderRepository.findByOrderTimestampBetween(start, end)
                .stream()
                .map(orderService::convertToDTO)
                .toList();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody Order orderDetails, Principal principal) {
        try {
            // Using a service method ensures items and totals are recalculated correctly
            Order updated = orderService.updateOrder(id, orderDetails, principal.getName());
            return ResponseEntity.ok(orderService.convertToDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/top-selling")
    public List<TopSellingItemDTO> getTopSelling() {
        return orderService.getTopSellingItems();
    }
}