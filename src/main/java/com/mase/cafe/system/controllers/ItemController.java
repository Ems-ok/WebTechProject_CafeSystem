package com.mase.cafe.system.controllers;

import com.mase.cafe.system.dtos.ItemDTO;
import com.mase.cafe.system.services.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manager/api/items")
public class ItemController {

    private final ItemService itemService;

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id, @Valid @RequestBody ItemDTO itemDto) {
        return ResponseEntity.ok(itemService.updateItem(id, itemDto));
    }

}