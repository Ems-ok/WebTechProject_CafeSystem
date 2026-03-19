package com.mase.cafe.system.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 5, message = "Order must be at least 5 characters long")
    @Column(name = "ordername", nullable = false)
    private String ordername;

    @Column(nullable = false)
    private LocalDateTime orderTimestamp;

    @Column(nullable = false)
    private Double totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        orderTimestamp = LocalDateTime.now();
    }
}