package com.mase.cafe.system.services;

import com.mase.cafe.system.dtos.UserDTO;
import com.mase.cafe.system.exceptions.SelfDeleteNotAllowedException;
import com.mase.cafe.system.exceptions.ValidationException;
import com.mase.cafe.system.models.User;
import com.mase.cafe.system.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private UserDTO convertToDto(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        return dto;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User applicationUser = userRepository.findByUsername(username);

        if (applicationUser == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return org.springframework.security.core.userdetails.User.withUsername(applicationUser.getUsername())
                .password(applicationUser.getPassword())
                .roles(applicationUser.getRole())
                .build();
    }

    public UserDTO getUserByUsername(String username) {
        return convertToDto(userRepository.findByUsername(username));
    }

    public List<UserDTO> getAllUsers() {
        Iterable<User> entities = userRepository.findAll();
        List<UserDTO> dtos = new ArrayList<>();
        for (User user : entities) {
            dtos.add(convertToDto(user));
        }
        return dtos;
    }

    public UserDTO getUserById(Long id) {
        return convertToDto(userRepository.findById(id).orElse(null));
    }

    public UserDTO createUser(UserDTO userDTO) {
        if (userDTO.getPassword() == null || userDTO.getPassword().trim().length() < 5) {
            throw new ValidationException("Password must be at least 5 characters long");
        }

        if (userRepository.findByUsername(userDTO.getUsername()) != null) {
            throw new ValidationException("Error: Username already exists");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole());

        return convertToDto(userRepository.save(user));
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) return null;

        existingUser.setUsername(userDTO.getUsername());
        existingUser.setRole(userDTO.getRole());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        return convertToDto(userRepository.save(existingUser));
    }


    public void deleteUser(Long id, String currentUsername) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            if (user.getUsername().equals(currentUsername)) {
                throw new SelfDeleteNotAllowedException(currentUsername);
            }
            userRepository.deleteById(id);
        }
    }
}