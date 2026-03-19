package com.mase.cafe.system.dtos;

import com.mase.cafe.system.models.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderDTO {
    private List<Order> orders;
    private double totalRevenue;
    private int orderCount;
}