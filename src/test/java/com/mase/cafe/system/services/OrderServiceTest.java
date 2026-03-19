package com.mase.cafe.system.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mase.cafe.system.dtos.OrderDTO;
import com.mase.cafe.system.models.Order;
import com.mase.cafe.system.models.User;
import com.mase.cafe.system.repositories.ItemRepository;
import com.mase.cafe.system.repositories.OrderRepository;
import com.mase.cafe.system.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private OrderService orderService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        orderRepository = mock(OrderRepository.class);
        userRepository = mock(UserRepository.class);

        orderService = new OrderService(orderRepository, userRepository,itemRepository);
    }

    @Test
    void getAllOrdersReturnsDtoList() {

        Order order = new Order();
        order.setOrdername("Table 1");
        order.setTotalAmount(10.0);

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order));

        List<OrderDTO> result = orderService.getAllOrders();

        assertEquals(1, result.size());
        assertEquals("Table 1", result.get(0).getOrdername());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void saveOrderSetsUserAndSaves() {

        String username = "staff1";
        User mockUser = new User();
        mockUser.setUsername(username);

        Order inputOrder = new Order();
        inputOrder.setOrdername("New Order");

        when(userRepository.findByUsername(username)).thenReturn(mockUser);
        when(orderRepository.save(any(Order.class))).thenReturn(inputOrder);

        Order savedOrder = orderService.saveOrder(inputOrder, username);

        assertNotNull(savedOrder);
        assertEquals(mockUser, inputOrder.getUser());
        verify(orderRepository, times(1)).save(inputOrder);
    }

    @Test
    void deleteOrderCallsRepository() {

        Long orderId = 1L;
        Order order = new Order();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.deleteOrder(orderId);

        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    void deleteOrderThrowsExceptionWhenNotFound() {

        Long orderId = 99L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            orderService.deleteOrder(orderId);
        });
        verify(orderRepository, never()).delete(any());
    }
}