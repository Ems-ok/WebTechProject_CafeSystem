package com.mase.cafe.system.repositories;

import com.mase.cafe.system.models.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

    List<Order> findByOrderTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Override
    List<Order> findAll();
}