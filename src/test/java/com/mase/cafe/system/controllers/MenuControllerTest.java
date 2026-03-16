package com.mase.cafe.system.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.mase.cafe.system.dtos.ItemDTO;
import com.mase.cafe.system.dtos.MenuDTO;
import com.mase.cafe.system.models.Item;
import com.mase.cafe.system.services.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

class MenuControllerTest {

    private MenuService menuService;
    private MenuController menuController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        menuService = mock(MenuService.class);
        menuController = new MenuController(menuService);
    }

    @Test
    void createAndAddReturnsUpdatedMenu() {
        LocalDate testDate = LocalDate.now();
        ItemDTO inputDto = new ItemDTO();
        inputDto.setName("Hot Chocolate");
        inputDto.setPrice(3.50);
        inputDto.setCategory("Beverage");

        MenuDTO expectedMenuDto = new MenuDTO();
        expectedMenuDto.setId(1L);

        when(menuService.createItemAndAddToMenu(eq(testDate), any(Item.class)))
                .thenReturn(expectedMenuDto);

        ResponseEntity<MenuDTO> response = menuController.createAndAdd(testDate, inputDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(menuService, times(1)).createItemAndAddToMenu(eq(testDate), any(Item.class));
    }

    @Test
    void getAllMenusReturnsListOfMenus() {
        List<MenuDTO> expectedMenus = Arrays.asList(new MenuDTO(), new MenuDTO());
        when(menuService.getAllMenus()).thenReturn(expectedMenus);

        List<MenuDTO> actualMenus = menuController.getAllMenus();

        assertEquals(2, actualMenus.size());
        verify(menuService, times(1)).getAllMenus();
    }
}