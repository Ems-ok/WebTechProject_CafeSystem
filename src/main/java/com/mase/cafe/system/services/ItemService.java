package com.mase.cafe.system.services;

import com.mase.cafe.system.dtos.ItemDTO;
import com.mase.cafe.system.exceptions.ResourceNotFoundException;
import com.mase.cafe.system.models.Item;
import com.mase.cafe.system.repositories.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public ItemDTO updateItem(Long id, ItemDTO details) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));

        item.setName(details.getName());
        item.setCategory(details.getCategory());
        item.setPrice(details.getPrice());
        item.setDescription(details.getDescription());

        Item savedItem = itemRepository.save(item);
        return convertToDTO(savedItem);
    }
    private ItemDTO convertToDTO(Item item) {
        return ItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .category(item.getCategory())
                .menuId(item.getMenu() != null ? item.getMenu().getId() : null)
                .build();
    }
}