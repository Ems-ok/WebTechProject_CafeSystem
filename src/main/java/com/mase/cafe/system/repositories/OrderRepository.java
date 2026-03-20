package com.mase.cafe.system.repositories;

import com.mase.cafe.system.dtos.TopSellingItemDTO;
import com.mase.cafe.system.models.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Override
    List<Order> findAll();

    List<Order> findByOrderTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new com.mase.cafe.system.dtos.TopSellingItemDTO(oi.item.name, SUM(oi.quantity)) " +
            "FROM OrderItem oi " +
            "GROUP BY oi.item.name " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<TopSellingItemDTO> findTopSellingItems(Pageable pageable);
}