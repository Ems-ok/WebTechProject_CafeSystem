package com.mase.cafe.system.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mase.cafe.system.dtos.ItemDTO;
import com.mase.cafe.system.exceptions.ResourceNotFoundException;
import com.mase.cafe.system.models.Item;
import com.mase.cafe.system.models.Menu;
import com.mase.cafe.system.repositories.ItemRepository;
import com.mase.cafe.system.repositories.MenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateItemSuccess() {

        Long itemId = 1L;

        Menu mockMenu = new Menu();
        mockMenu.setId(10L);

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setName("Old Name");
        existingItem.setMenu(mockMenu);

        ItemDTO updateDetails = ItemDTO.builder()
                .name("New Name")
                .category("Beverage")
                .price(5.50)
                .description("New Description")
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDTO result = itemService.updateItem(itemId, updateDetails);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("Beverage", result.getCategory());
        assertEquals(5.50, result.getPrice());
        assertEquals(10L, result.getMenuId());

        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(1)).save(existingItem);
    }

    @Test
    void updateItemNotFoundThrowsException() {

        Long itemId = 99L;
        ItemDTO updateDetails = new ItemDTO();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            itemService.updateItem(itemId, updateDetails);
        });

        assertEquals("Item not found with id: 99", exception.getMessage());
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void deleteItemShouldRemoveFromMenusAndDeleteItem() {

        Long itemId = 1L;
        Item mockItem = new Item();
        mockItem.setId(itemId);
        mockItem.setName("Hot Chocolate");

        Menu mockMenu = new Menu();
        mockMenu.setItems(new HashSet<>(List.of(mockItem)));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(mockItem));
        when(menuRepository.findByItemsContaining(mockItem)).thenReturn(List.of(mockMenu));

        itemService.deleteItem(itemId);

        assertFalse(mockMenu.getItems().contains(mockItem), "Item should be removed from the menu list");

        verify(itemRepository, times(1)).findById(itemId);
        verify(menuRepository, times(1)).findByItemsContaining(mockItem);
        verify(menuRepository, times(1)).save(mockMenu);
        verify(itemRepository, times(1)).delete(mockItem);
    }
}