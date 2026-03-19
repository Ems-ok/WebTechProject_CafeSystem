package com.mase.cafe.system.services;
import com.mase.cafe.system.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

}