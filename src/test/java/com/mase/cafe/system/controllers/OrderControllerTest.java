package com.mase.cafe.system.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mase.cafe.system.dtos.OrderDTO;
import com.mase.cafe.system.models.Order;
import com.mase.cafe.system.models.User;
import com.mase.cafe.system.repositories.OrderRepository;
import com.mase.cafe.system.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Optional;

class OrderControllerTest {

    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private OrderController orderController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        orderRepository = mock(OrderRepository.class);
        userRepository = mock(UserRepository.class);

        orderController = new OrderController(userRepository, orderRepository);
    }

    @Test
    void createOrderReturnsCreatedStatus() {
        String username = "testuser";
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        User mockUser = new User();
        mockUser.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(mockUser);

        Order inputOrder = new Order();
        inputOrder.setOrdername("Table 5 - Coffee");
        inputOrder.setTotalAmount(15.50);

        when(orderRepository.save(any(Order.class))).thenReturn(inputOrder);

        ResponseEntity<?> response = orderController.createOrder(inputOrder, mockPrincipal);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void getOrderByIdReturnsOrderDto() {

        Long orderId = 1L;
        Order mockOrder = new Order();
        mockOrder.setId(orderId);
        mockOrder.setOrdername("Expresso Shot");
        mockOrder.setTotalAmount(3.50);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        ResponseEntity<?> response = orderController.getOrderById(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof OrderDTO);
        OrderDTO resultDto = (OrderDTO) response.getBody();
        assertEquals("Expresso Shot", resultDto.getOrdername());

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void updateOrderReturnsOk() {

        Long orderId = 1L;
        Order existingOrder = new Order();
        existingOrder.setId(orderId);

        Order updatedDetails = new Order();
        updatedDetails.setOrdername("Updated Table Name");
        updatedDetails.setTotalAmount(20.00);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        ResponseEntity<?> response = orderController.updateOrder(orderId, updatedDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderRepository, times(1)).save(existingOrder);
        assertEquals("Updated Table Name", existingOrder.getOrdername());
    }

    @Test
    void deleteOrderReturnsOk() {

        Long orderId = 1L;
        Order mockOrder = new Order();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        ResponseEntity<?> response = orderController.deleteOrder(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderRepository, times(1)).delete(mockOrder);
    }

    @Test
    void getOrderByIdReturnsNotFoundWhenMissing() {

        Long orderId = 99L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = orderController.getOrderById(orderId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}