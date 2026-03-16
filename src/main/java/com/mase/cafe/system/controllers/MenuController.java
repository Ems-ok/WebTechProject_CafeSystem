package com.mase.cafe.system.controllers;

import com.mase.cafe.system.dtos.ItemDTO;
import com.mase.cafe.system.dtos.MenuDTO; // Import your DTO
import com.mase.cafe.system.models.Item;
import com.mase.cafe.system.services.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/manager/api/menus")
@PreAuthorize("hasRole('MANAGER')")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/create-and-add")
    public ResponseEntity<MenuDTO> createAndAdd(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Valid @RequestBody ItemDTO itemDto) {

        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setPrice(itemDto.getPrice());
        item.setCategory(itemDto.getCategory());

        MenuDTO updatedMenuDto = menuService.createItemAndAddToMenu(date, item);
        return ResponseEntity.ok(updatedMenuDto);
    }


    @GetMapping
    public List<MenuDTO> getAllMenus() {
        return menuService.getAllMenus();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuDTO> getMenuById(@PathVariable Long id) {
        return menuService.getMenuById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}