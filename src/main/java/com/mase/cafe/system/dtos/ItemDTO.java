package com.mase.cafe.system.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    private Long id;

    @NotBlank(message = "Item name is required")
    private String name;

    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be positive")
    private Double price;

    @NotBlank(message = "Category is required")
    private String category;

    private Long menuId;
}