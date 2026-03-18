package com.mase.cafe.system.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.mase.cafe.system.models.Menu;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MenuRepository extends CrudRepository<Menu, Long> {

    Optional<Menu> findByMenuDate(LocalDate menuDate);

    Menu saveAndFlush(Menu menu);

    void flush();
}