package com.mase.cafe.system.controllers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private LoginController loginController;

    @BeforeEach
    void setup() {
        loginController = new LoginController();
    }

    @Test
    void getLoginReturnsLoginView() {

        String viewName = loginController.getLogin();
        assertEquals("login", viewName, "The login view name should be 'login'");
    }
}