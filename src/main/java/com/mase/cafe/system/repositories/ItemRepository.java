package com.mase.cafe.system.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.mase.cafe.system.models.Item;

@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {


}