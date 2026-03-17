package com.mase.cafe.system.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "menus")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    @NotNull(message = "Date is required")
    @Column(name = "menu_date", unique = true, nullable = false)
    private LocalDate menuDate;

    @ManyToMany
    @JoinTable(
            name = "menu_items_link",
            joinColumns = @JoinColumn(name = "menu_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private Set<Item> items = new HashSet<>();
}