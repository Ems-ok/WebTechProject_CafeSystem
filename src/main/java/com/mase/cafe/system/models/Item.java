package com.mase.cafe.system.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "items")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @NotBlank(message = "Item name is required")
    @Column(nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be positive")
    @Column(nullable = false)
    private double price;

    @NotBlank(message = "Category is required (e.g., Beverage, Pastry)")
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

}