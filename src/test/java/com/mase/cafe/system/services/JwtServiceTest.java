package com.mase.cafe.system.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setup() {
        jwtService = new JwtService();

        String secret = "dGhpc2lzYXZlcnlzdXBlcnNlY3JldGtleWF0bGVhc3QxMjM0";
        ReflectionTestUtils.setField(jwtService, "secret", secret);
    }

    @Test
    void generateTokenAndExtractUsername() {
        User userDetails = new User("manager", "password", java.util.List.of());

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);

        String username = jwtService.getUsernameFromToken(token);
        assertEquals("manager", username);
    }

    @Test
    void getExpirationDateFromTokenReturnsFutureDate() {
        User userDetails = new User("staff", "password", java.util.List.of());
        String token = jwtService.generateToken(userDetails);

        Date expiration = jwtService.getExpirationDateFromToken(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()), "Expiration should be in the future");
    }

    @Test
    void validateTokenReturnsTrueForValidToken() {
        User userDetails = new User("manager", "password", java.util.List.of());
        String token = jwtService.generateToken(userDetails);

        boolean valid = jwtService.validateToken(token, userDetails);
        assertTrue(valid);
    }

    @Test
    void validateTokenReturnsFalseForWrongUsername() {
        User userDetails = new User("manager", "password", java.util.List.of());
        User wrongUser = new User("staff", "password", java.util.List.of());

        String token = jwtService.generateToken(userDetails);

        boolean valid = jwtService.validateToken(token, wrongUser);
        assertFalse(valid);
    }

    @Test
    void validateTokenReturnsFalseForExpiredToken() {
        User userDetails = new User("manager", "password", java.util.List.of());

        String token = io.jsonwebtoken.Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // expired
                .signWith(
                        io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                                java.util.Base64.getDecoder().decode(
                                        "dGhpc2lzYXZlcnlzdXBlcnNlY3JldGtleWF0bGVhc3QxMjM0"
                                )
                        ),
                        io.jsonwebtoken.SignatureAlgorithm.HS256
                )
                .compact();

        //catch the exception
        boolean valid;
        try {
            valid = jwtService.validateToken(token, userDetails);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            valid = false; // expired token - invalid
        }

        assertFalse(valid, "Expired token should be invalid");
    }
}
