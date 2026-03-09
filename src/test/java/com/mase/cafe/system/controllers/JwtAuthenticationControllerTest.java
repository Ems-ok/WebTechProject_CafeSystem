package com.mase.cafe.system.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mase.cafe.system.dtos.JwtRequest;
import com.mase.cafe.system.dtos.JwtResponse;
import com.mase.cafe.system.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

class JwtAuthenticationControllerTest {

    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private JwtAuthenticationController controller;

    private Authentication authentication;
    private User userDetails;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        authenticationManager = mock(AuthenticationManager.class);
        jwtService = mock(JwtService.class);
        controller = new JwtAuthenticationController(authenticationManager, jwtService);

        authentication = mock(Authentication.class);
        userDetails = new User("manager", "password",
                java.util.Collections.emptyList());
    }

    @Test
    void loginReturnsJwtResponse() {

        JwtRequest request = new JwtRequest();
        request.setUsername("manager");
        request.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        ResponseEntity<JwtResponse> response = controller.login(request);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().getToken());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authentication, times(1)).getPrincipal();
        verify(jwtService, times(1)).generateToken(userDetails);
        verifyNoMoreInteractions(authenticationManager, authentication, jwtService);
    }
}
