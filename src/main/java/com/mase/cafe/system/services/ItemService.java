package com.mase.cafe.system.services;

import com.mase.cafe.system.dtos.ItemDTO;
import com.mase.cafe.system.exceptions.ResourceNotFoundException;
import com.mase.cafe.system.models.Item;
import com.mase.cafe.system.models.Menu;
import com.mase.cafe.system.repositories.ItemRepository;
import com.mase.cafe.system.repositories.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final MenuRepository menuRepository;

    public List<ItemDTO> getAllItems() {
        return ((List<Item>) itemRepository.findAll())
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

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

    @Transactional
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));

        List<Menu> menus = menuRepository.findByItemsContaining(item);
        for (Menu menu : menus) {
            menu.getItems().remove(item);
            menuRepository.save(menu);
        }

        itemRepository.delete(item);
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