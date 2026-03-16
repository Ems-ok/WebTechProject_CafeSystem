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
import java.util.stream.Collectors;

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

        newItem.setMenu(menu);

        boolean alreadyExists = menu.getItems().stream()
                .anyMatch(item -> item.getName().equalsIgnoreCase(newItem.getName()));

        if (alreadyExists) {
            throw new IllegalArgumentException("Item '" + newItem.getName() + "' is already on the menu for " + date);
        }

        Item savedItem = itemRepository.save(newItem);

        if (menu.getItems() == null) {
            menu.setItems(new HashSet<>());
        }
        menu.getItems().add(savedItem);

        Menu savedMenu = menuRepository.save(menu);

        return convertToDTO(savedMenu);
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
        if (menu == null) return null;

        MenuDTO dto = new MenuDTO();
        dto.setId(menu.getId());
        dto.setMenuDate(menu.getMenuDate());

        Set<Item> items = menu.getItems() != null ? menu.getItems() : new HashSet<>();

        Set<ItemDTO> itemDtos = items.stream().map(item -> {
            ItemDTO itemDto = new ItemDTO();
            itemDto.setId(item.getId());
            itemDto.setName(item.getName());
            itemDto.setDescription(item.getDescription());
            itemDto.setPrice(item.getPrice());
            itemDto.setCategory(item.getCategory());
            return itemDto;
        }).collect(Collectors.toSet());

        dto.setItems(itemDtos);
        return dto;
    }

    public MenuDTO createMenu(LocalDate menuDate) {

        if (menuRepository.findByMenuDate(menuDate).isPresent()) {

            throw new IllegalArgumentException("A menu for this date already exists: " + menuDate);
        }

        Menu menu = new Menu();
        menu.setMenuDate(menuDate);
        menu.setItems(new HashSet<>());

        Menu savedMenu = menuRepository.save(menu);

        return convertToDTO(savedMenu);
    }
    public MenuDTO getMenuByDate(LocalDate date) {
        return menuRepository.findByMenuDate(date)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No menu found for date: " + date));
    }
}