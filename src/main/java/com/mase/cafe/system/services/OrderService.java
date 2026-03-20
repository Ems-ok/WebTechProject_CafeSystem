package com.mase.cafe.system.services;

import com.mase.cafe.system.dtos.OrderDTO;
import com.mase.cafe.system.exceptions.ValidationException;
import com.mase.cafe.system.models.Item;
import com.mase.cafe.system.models.Order;
import com.mase.cafe.system.models.OrderItem;
import com.mase.cafe.system.models.User;
import com.mase.cafe.system.repositories.ItemRepository;
import com.mase.cafe.system.repositories.OrderRepository;
import com.mase.cafe.system.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import this

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional
    public Order saveOrder(Order order, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new RuntimeException("User not found");
        order.setUser(user);

        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            double calculatedTotal = 0;

            for (OrderItem uiItem : order.getOrderItems()) {
                Item databaseItem = itemRepository.findByName(uiItem.getItem().getName())
                        .orElseThrow(() -> new ValidationException("Item not found: " + uiItem.getItem().getName()));

                uiItem.setItem(databaseItem);
                uiItem.setOrder(order);
                calculatedTotal += databaseItem.getPrice() * uiItem.getQuantity();
            }
            order.setTotalAmount(calculatedTotal);
        }

        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrder(Long id, Order orderDetails, String username) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        existingOrder.setOrdername(orderDetails.getOrdername());

        existingOrder.getOrderItems().clear();

        if (orderDetails.getOrderItems() != null) {
            double calculatedTotal = 0;
            for (OrderItem uiItem : orderDetails.getOrderItems()) {
                Item databaseItem = itemRepository.findByName(uiItem.getItem().getName())
                        .orElseThrow(() -> new ValidationException("Item not found: " + uiItem.getItem().getName()));

                uiItem.setItem(databaseItem);
                uiItem.setOrder(existingOrder); // Link item back to this order
                existingOrder.getOrderItems().add(uiItem);

                calculatedTotal += databaseItem.getPrice() * uiItem.getQuantity();
            }
            existingOrder.setTotalAmount(calculatedTotal);
        }

        return orderRepository.save(existingOrder);
    }

    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        orderRepository.delete(order);
    }

    public OrderDTO convertToDTO(Order order) {
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