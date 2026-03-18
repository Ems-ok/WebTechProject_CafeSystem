package com.mase.cafe.system.services;

import com.mase.cafe.system.dtos.MenuDTO;
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
import java.util.Set;

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
        testItem.setDescription("test");
        testItem.setCategory("Beverage");
        testItem.setMenu(testMenu);
    }

    @Test
    void createItemAndAddToMenuSuccess() {

        when(menuRepository.findByMenuDate(testDate)).thenReturn(Optional.of(testMenu));

        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(menuRepository.save(any(Menu.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MenuDTO result = menuService.createItemAndAddToMenu(testDate, testItem);

        assertNotNull(result, "The returned MenuDTO should not be null");
        assertEquals(testDate, result.getMenuDate());

        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());

        verify(menuRepository).findByMenuDate(testDate);
        verify(menuRepository).save(any(Menu.class));
    }

    @Test
    void createItemAndAddToMenuAutoCreatesMenuWhenNotFound() {

        when(menuRepository.findByMenuDate(testDate)).thenReturn(Optional.empty());

        when(menuRepository.save(any(Menu.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        MenuDTO result = menuService.createItemAndAddToMenu(testDate, testItem);

        assertNotNull(result);
        assertEquals(testDate, result.getMenuDate());
        assertEquals(1, result.getItems().size());

        verify(menuRepository, times(2)).save(any(Menu.class));
        verify(itemRepository).save(testItem);
    }

    @Test
    void createItemAndAddToMenuThrowsExceptionForDuplicateItemName() {

        Item existingItem = new Item();
        existingItem.setId(101L);
        existingItem.setName("Latte");
        existingItem.setPrice(4.50);
        existingItem.setDescription("test");
        existingItem.setCategory("Beverage");

        testMenu.setItems(new HashSet<>(Set.of(existingItem)));

        when(menuRepository.findByMenuDate(testDate)).thenReturn(Optional.of(testMenu));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menuService.createItemAndAddToMenu(testDate, testItem);
        });

        assertTrue(exception.getMessage().contains("already on the menu"));

        verify(itemRepository, never()).save(any());
        verify(menuRepository, never()).save(testMenu);
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