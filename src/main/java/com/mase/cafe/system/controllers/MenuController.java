package com.mase.cafe.system.controllers;

import com.mase.cafe.system.dtos.ItemDTO;
import com.mase.cafe.system.dtos.MenuDTO;
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
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<MenuDTO> createMenu(@RequestBody MenuDTO menuDto) {
        MenuDTO savedMenu = menuService.createMenu(menuDto.getMenuDate());
        return ResponseEntity.status(201).body(savedMenu);
    }

    @PostMapping("/create-and-add")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<MenuDTO> createAndAdd(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
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
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public List<MenuDTO> getAllMenus() {
        return menuService.getAllMenus();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<MenuDTO> getMenuById(@PathVariable Long id) {
        return menuService.getMenuById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/date")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<MenuDTO> getMenuByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        MenuDTO menu = menuService.getMenuByDate(date);
        return ResponseEntity.ok(menu);
    }
}