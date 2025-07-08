package com.paymybuddy.paymybuddy.service;

import com.paymybuddy.paymybuddy.config.SecurityConfig;
import com.paymybuddy.paymybuddy.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    private UserService userService;
    private SecurityConfig securityConfig;
    private LoginService loginService;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        passwordEncoder = mock(BCryptPasswordEncoder.class);
        securityConfig = mock(SecurityConfig.class);

        when(securityConfig.passwordEncoder()).thenReturn(passwordEncoder);

        loginService = new LoginService(userService, securityConfig);
    }

    @Test
    void login_shouldSucceed_whenCredentialsAreCorrect() {
        String email = "test@mail.com";
        String rawPassword = "password";
        String encodedPassword = "encodedPassword";
        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        when(userService.getUserByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        assertDoesNotThrow(() -> loginService.login(email, rawPassword));
    }

    @Test
    void login_shouldThrow_whenUserNotFound() {
        String email = "notfound@mail.com";
        when(userService.getUserByEmail(email)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> loginService.login(email, "any"));
        assertEquals("Invalid username or password", ex.getMessage());
    }

    @Test
    void login_shouldThrow_whenPasswordIsIncorrect() {
        String email = "test@mail.com";
        String rawPassword = "wrong";
        String encodedPassword = "encodedPassword";
        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        when(userService.getUserByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> loginService.login(email, rawPassword));
        assertEquals("Invalid username or password", ex.getMessage());
    }

    @Test
    void login_shouldThrow_whenEmailIsNull() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> loginService.login(null, "password"));
        assertEquals("Invalid username or password", ex.getMessage());
    }

    @Test
    void login_shouldThrow_whenPasswordIsNull() {
        String email = "test@mail.com";
        Exception ex = assertThrows(IllegalArgumentException.class, () -> loginService.login(email, null));
        assertEquals("Invalid username or password", ex.getMessage());
    }
}
