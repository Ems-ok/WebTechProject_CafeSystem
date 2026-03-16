package com.mase.cafe.system.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mase.cafe.system.dtos.UserDTO;
import com.mase.cafe.system.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

class UserControllerTest {

    private UserService userService;
    private UserController userController;
    private Authentication authentication;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userService = mock(UserService.class);
        authentication = mock(Authentication.class);
        userController = new UserController(userService);

        when(authentication.getName()).thenReturn("manager");

        Collection<GrantedAuthority> authorities = List.of(
                (GrantedAuthority) () -> "MANAGER"
        );
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
    }

    @Test
    void getAllUsersReturnsUserList() {
        List<UserDTO> expectedUsers = Arrays.asList(new UserDTO(), new UserDTO());
        when(userService.getAllUsers()).thenReturn(expectedUsers);

        List<UserDTO> actualUsers = userController.getAllUsers();

        assertEquals(expectedUsers, actualUsers);
        verify(userService, times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getUserByIdReturnsUserWhenFound() {
        UserDTO user = new UserDTO();
        when(userService.getUserById(1L)).thenReturn(user);

        ResponseEntity<UserDTO> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(user, response.getBody());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getUserByIdReturnsNotFoundWhenUserMissing() {
        when(userService.getUserById(1L)).thenReturn(null);

        ResponseEntity<UserDTO> response = userController.getUserById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void createUserReturnsCreatedUser() {
        UserDTO inputUser = new UserDTO();
        UserDTO savedUser = new UserDTO();
        when(userService.createUser(inputUser)).thenReturn(savedUser);

        ResponseEntity<UserDTO> response = userController.createUser(inputUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(savedUser, response.getBody());
        verify(userService, times(1)).createUser(inputUser);
    }

    @Test
    void updateUserReturnsUpdatedUserWhenFound() {
        UserDTO inputUser = new UserDTO();
        UserDTO updatedUser = new UserDTO();
        when(userService.updateUser(1L, inputUser)).thenReturn(updatedUser);

        ResponseEntity<UserDTO> response = userController.updateUser(1L, inputUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updatedUser, response.getBody());
        verify(userService, times(1)).updateUser(1L, inputUser);
    }

    @Test
    void updateUserReturnsNotFoundWhenUserMissing() {
        UserDTO inputUser = new UserDTO();
        when(userService.updateUser(1L, inputUser)).thenReturn(null);

        ResponseEntity<UserDTO> response = userController.updateUser(1L, inputUser);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).updateUser(1L, inputUser);
    }

    @Test
    void deleteUserReturnsNoContentOnSuccess() {
        doNothing().when(userService).deleteUser(1L, "manager");

        ResponseEntity<Void> response = userController.deleteUser(1L, authentication);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser(1L, "manager");
    }

    @Test
    void deleteUserReturnsForbiddenOnException() {
        doThrow(new RuntimeException()).when(userService).deleteUser(1L, "manager");

        ResponseEntity<Void> response = userController.deleteUser(1L, authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(userService, times(1)).deleteUser(1L, "manager");
    }
}