package com.mase.cafe.system.services;

import com.mase.cafe.system.dtos.OrderDTO;
import com.mase.cafe.system.models.Order;
import com.mase.cafe.system.models.User;
import com.mase.cafe.system.repositories.OrderRepository;
import com.mase.cafe.system.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Order saveOrder(Order order, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new RuntimeException("User not found");

        order.setUser(user);
        if (order.getOrderItems() != null) {
            order.getOrderItems().forEach(item -> item.setOrder(order));
        }
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        orderRepository.delete(order);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrdername(order.getOrdername());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderTimestamp(order.getOrderTimestamp());
        if (order.getUser() != null) {
            dto.setUsername(order.getUser().getUsername());
        }
        return dto;
    }
}