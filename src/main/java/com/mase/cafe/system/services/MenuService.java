package com.mase.cafe.system.services;

import com.mase.cafe.system.dtos.ItemDTO;
import com.mase.cafe.system.dtos.MenuDTO;
import com.mase.cafe.system.exceptions.ResourceNotFoundException;
import com.mase.cafe.system.models.Item;
import com.mase.cafe.system.models.Menu;
import com.mase.cafe.system.repositories.ItemRepository;
import com.mase.cafe.system.repositories.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public MenuDTO createItemAndAddToMenu(LocalDate date, Item newItem) {

        Menu menu = menuRepository.findByMenuDate(date)
                .orElseGet(() -> {
                    Menu newMenu = new Menu();
                    newMenu.setMenuDate(date);
                    newMenu.setItems(new HashSet<>());
                    return menuRepository.save(newMenu);
                });

        Item savedItem = itemRepository.save(newItem);
        menu.getItems().add(savedItem);
        menuRepository.save(menu);

        return convertToDTO(menu);
    }

    public List<MenuDTO> getAllMenus() {
        Iterable<Menu> menus = menuRepository.findAll();
        List<MenuDTO> menuList = new ArrayList<>();

        for (Menu menu : menus) {
            menuList.add(convertToDTO(menu));
        }

        return menuList;
    }

    public Optional<MenuDTO> getMenuById(Long id) {
        Optional<Menu> menuOptional = menuRepository.findById(id);
        if (menuOptional.isPresent()) {
            return Optional.of(convertToDTO(menuOptional.get()));
        }

        return Optional.empty();
    }

    private MenuDTO convertToDTO(Menu menu) {
        MenuDTO dto = new MenuDTO();
        dto.setId(menu.getId());
        dto.setMenuDate(menu.getMenuDate());

        Set<ItemDTO> itemDtos = new HashSet<>();

        for (Item item : menu.getItems()) {
            ItemDTO itemDto = new ItemDTO();
            itemDto.setId(item.getId());
            itemDto.setName(item.getName());
            itemDto.setDescription(item.getDescription());
            itemDto.setPrice(item.getPrice());
            itemDto.setCategory(item.getCategory());

            itemDtos.add(itemDto);
        }

        dto.setItems(itemDtos);
        return dto;
    }
}