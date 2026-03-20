package com.mase.cafe.system.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mase.cafe.system.dtos.OrderDTO;
import com.mase.cafe.system.models.Order;
import com.mase.cafe.system.repositories.OrderRepository;
import com.mase.cafe.system.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Optional;

class OrderControllerTest {

    private OrderRepository orderRepository;
    private OrderService orderService;
    private OrderController orderController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        orderRepository = mock(OrderRepository.class);
        orderService = mock(OrderService.class);

        orderController = new OrderController(orderService, orderRepository);
    }

    @Test
    void createOrderReturnsCreatedStatus() {

        String username = "testuser";
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        Order inputOrder = new Order();
        inputOrder.setOrdername("Table 5 - Coffee");

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setOrdername("Table 5 - Coffee");

        OrderDTO expectedDto = new OrderDTO();
        expectedDto.setId(1L);
        expectedDto.setOrdername("Table 5 - Coffee");

        when(orderService.saveOrder(any(Order.class), eq(username))).thenReturn(savedOrder);
        when(orderService.convertToDTO(savedOrder)).thenReturn(expectedDto);

        ResponseEntity<?> response = orderController.createOrder(inputOrder, mockPrincipal);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService, times(1)).saveOrder(any(Order.class), eq(username));
    }

    @Test
    void getOrderByIdReturnsOrderDto() {

        Long orderId = 1L;
        Order mockOrder = new Order();
        mockOrder.setId(orderId);
        mockOrder.setOrdername("Expresso Shot");

        OrderDTO mockDto = new OrderDTO();
        mockDto.setId(orderId);
        mockDto.setOrdername("Expresso Shot");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(orderService.convertToDTO(mockOrder)).thenReturn(mockDto);

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
        String username = "testuser";
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        Order updatedDetails = new Order();
        updatedDetails.setOrdername("Updated Table Name");

        Order updatedOrder = new Order();
        updatedOrder.setId(orderId);
        updatedOrder.setOrdername("Updated Table Name");

        OrderDTO resultDto = new OrderDTO();
        resultDto.setOrdername("Updated Table Name");

        when(orderService.updateOrder(eq(orderId), any(Order.class), eq(username))).thenReturn(updatedOrder);
        when(orderService.convertToDTO(updatedOrder)).thenReturn(resultDto);

        ResponseEntity<?> response = orderController.updateOrder(orderId, updatedDetails, mockPrincipal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService, times(1)).updateOrder(eq(orderId), any(Order.class), eq(username));
        assertEquals("Updated Table Name", ((OrderDTO)response.getBody()).getOrdername());
    }

    @Test
    void deleteOrderReturnsOk() {

        Long orderId = 1L;

        doNothing().when(orderService).deleteOrder(orderId);

        ResponseEntity<?> response = orderController.deleteOrder(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService, times(1)).deleteOrder(orderId);
    }

    @Test
    void getOrderByIdReturnsNotFoundWhenMissing() {

        Long orderId = 99L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = orderController.getOrderById(orderId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}