package com.mase.cafe.system.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mase.cafe.system.dtos.ItemDTO;
import com.mase.cafe.system.services.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ItemControllerTest {

    private ItemService itemService;
    private ItemController itemController;

    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);
        itemService = mock(ItemService.class);

        itemController = new ItemController(itemService);
    }

    @Test
    void updateItemReturnsUpdatedItem() {

        Long itemId = 1L;
        ItemDTO inputDto = new ItemDTO();
        inputDto.setName("Updated Latte");
        inputDto.setPrice(4.50);

        ItemDTO mockOutputDto = new ItemDTO();
        mockOutputDto.setId(itemId);
        mockOutputDto.setName("Updated Latte");
        mockOutputDto.setPrice(4.50);

        when(itemService.updateItem(eq(itemId), any(ItemDTO.class))).thenReturn(mockOutputDto);

        ResponseEntity<ItemDTO> response = itemController.updateItem(itemId, inputDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Latte", response.getBody().getName());
        assertEquals(itemId, response.getBody().getId());

        verify(itemService, times(1)).updateItem(eq(itemId), any(ItemDTO.class));
    }
}