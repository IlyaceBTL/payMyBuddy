package com.paymybuddy.paymybuddy.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

    @Test
    void showLoginPage_shouldReturnLoginView() {
        LoginController controller = new LoginController();
        String view = controller.showLoginPage();
        assertEquals("login", view);
    }
}

