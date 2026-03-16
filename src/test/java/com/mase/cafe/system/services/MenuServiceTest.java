package com.mase.cafe.system.services;

import com.mase.cafe.system.dtos.MenuDTO;
import com.mase.cafe.system.exceptions.ResourceNotFoundException;
import com.mase.cafe.system.models.Item;
import com.mase.cafe.system.models.Menu;
import com.mase.cafe.system.repositories.ItemRepository;
import com.mase.cafe.system.repositories.MenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private MenuService menuService;

    private Menu testMenu;
    private Item testItem;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.of(2026, 3, 15);

        testMenu = new Menu();
        testMenu.setId(1L);
        testMenu.setMenuDate(testDate);
        testMenu.setItems(new HashSet<>());

        testItem = new Item();
        testItem.setId(100L);
        testItem.setName("Latte");
        testItem.setPrice(4.50);
    }

    @Test
    void createItemAndAddToMenuSuccess() {

        when(menuRepository.findByMenuDate(testDate)).thenReturn(Optional.of(testMenu));
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        MenuDTO result = menuService.createItemAndAddToMenu(testDate, testItem);

        assertNotNull(result);
        assertEquals(testDate, result.getMenuDate());
        assertEquals(1, result.getItems().size());

        verify(menuRepository).findByMenuDate(testDate);
        verify(itemRepository).save(testItem);
        verify(menuRepository).save(testMenu);
    }

    @Test
    void createItemAndAddToMenuThrowsExceptionWhenMenuNotFound() {

        when(menuRepository.findByMenuDate(testDate)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            menuService.createItemAndAddToMenu(testDate, testItem);
        });

        assertTrue(exception.getMessage().contains("Menu not found for date"));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void getMenuByIdReturnsDtoWhenIdExists() {

        when(menuRepository.findById(1L)).thenReturn(Optional.of(testMenu));

        Optional<MenuDTO> result = menuService.getMenuById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void getMenuByIdReturnsEmptyWhenIdDoesNotExist() {

        when(menuRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<MenuDTO> result = menuService.getMenuById(99L);

        assertFalse(result.isPresent());
    }
}