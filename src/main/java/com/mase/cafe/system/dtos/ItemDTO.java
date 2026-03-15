package com.mase.cafe.system.dtos;

import lombok.Data;

@Data
public class ItemDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String category;
}