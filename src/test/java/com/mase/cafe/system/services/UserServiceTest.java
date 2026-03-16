package com.mase.cafe.system.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mase.cafe.system.dtos.UserDTO;
import com.mase.cafe.system.exceptions.SelfDeleteNotAllowedException;
import com.mase.cafe.system.exceptions.ValidationException;
import com.mase.cafe.system.models.User;
import com.mase.cafe.system.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void loadUserByUsernameReturnsUserDetails() {
        User user = new User();
        user.setUsername("manager");
        user.setPassword("encoded-password");
        user.setRole("MANAGER");

        when(userRepository.findByUsername("manager")).thenReturn(user);

        UserDetails details = userService.loadUserByUsername("manager");

        assertEquals("manager", details.getUsername());
        assertEquals("encoded-password", details.getPassword());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER")));
    }

    @Test
    void loadUserByUsernameThrowsIfNotFound() {
        when(userRepository.findByUsername("missing")).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("missing"));
    }

    @Test
    void getUserByUsernameReturnsDto() {
        User user = new User();
        user.setUsername("staff");
        user.setRole("STAFF");
        when(userRepository.findByUsername("staff")).thenReturn(user);

        UserDTO dto = userService.getUserByUsername("staff");

        assertEquals("staff", dto.getUsername());
        assertEquals("STAFF", dto.getRole());
    }

    @Test
    void getUserByUsernameReturnsNullIfNotFound() {
        when(userRepository.findByUsername("missing")).thenReturn(null);
        UserDTO dto = userService.getUserByUsername("missing");
        assertNull(dto);
    }


    @Test
    void getAllUsersReturnsDtoList() {
        User u1 = new User(); u1.setUsername("user1"); u1.setRole("STAFF");
        User u2 = new User(); u2.setUsername("user2"); u2.setRole("MANAGER");
        when(userRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        var list = userService.getAllUsers();
        assertEquals(2, list.size());
        assertEquals("user1", list.get(0).getUsername());
        assertEquals("STAFF", list.get(0).getRole());
        assertEquals("user2", list.get(1).getUsername());
        assertEquals("MANAGER", list.get(1).getRole());
    }


    @Test
    void getUserByIdReturnsDto() {
        User u = new User(); u.setId(1L); u.setUsername("user"); u.setRole("STAFF");
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));

        UserDTO dto = userService.getUserById(1L);
        assertEquals(1L, dto.getId());
        assertEquals("user", dto.getUsername());
        assertEquals("STAFF", dto.getRole());
    }

    @Test
    void getUserByIdReturnsNullIfNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertNull(userService.getUserById(1L));
    }


    @Test
    void createUserSucceeds() {
        UserDTO dto = new UserDTO();
        dto.setUsername("newstaff");
        dto.setPassword("password");
        dto.setRole("STAFF");

        when(userRepository.findByUsername("newstaff")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDTO saved = userService.createUser(dto);
        assertEquals("newstaff", saved.getUsername());
        assertEquals("STAFF", saved.getRole());
    }

    @Test
    void createUserThrowsIfPasswordTooShort() {
        UserDTO dto = new UserDTO();
        dto.setUsername("user");
        dto.setPassword("1234");
        dto.setRole("STAFF");
        assertThrows(ValidationException.class, () -> userService.createUser(dto));
    }

    @Test
    void createUserThrowsIfUsernameExists() {
        UserDTO dto = new UserDTO();
        dto.setUsername("existing");
        dto.setPassword("12345");
        dto.setRole("STAFF");

        when(userRepository.findByUsername("existing")).thenReturn(new User());

        assertThrows(ValidationException.class, () -> userService.createUser(dto));
    }


    @Test
    void updateUserSucceeds() {
        User existing = new User();
        existing.setId(1L); existing.setUsername("old"); existing.setRole("STAFF");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newpass")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDTO dto = new UserDTO();
        dto.setUsername("new"); dto.setPassword("newpass"); dto.setRole("MANAGER");

        UserDTO updated = userService.updateUser(1L, dto);

        assertEquals("new", updated.getUsername());
        assertEquals("MANAGER", updated.getRole());
    }

    @Test
    void updateUserReturnsNullIfNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserDTO dto = new UserDTO();
        assertNull(userService.updateUser(1L, dto));
    }


    @Test
    void deleteUserSucceeds() {
        User u = new User(); u.setId(1L); u.setUsername("staffuser");
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));

        userService.deleteUser(1L, "manager");
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUserThrowsSelfDelete() {
        User u = new User(); u.setId(1L); u.setUsername("manager");
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));

        assertThrows(SelfDeleteNotAllowedException.class, () -> userService.deleteUser(1L, "manager"));
    }

    @Test
    void deleteUserDoesNothingIfNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> userService.deleteUser(1L, "manager"));
        verify(userRepository, never()).deleteById(anyLong());
    }
}