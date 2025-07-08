package com.paymybuddy.paymybuddy.service;

import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.UserRepository;
import com.paymybuddy.paymybuddy.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;

    private SecurityConfig securityConfig;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        securityConfig = mock(SecurityConfig.class);
        when(securityConfig.passwordEncoder()).thenReturn(mock(org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.class));
        userService = new UserService(userRepository, securityConfig);
    }

    @Test
    void testGetUserById() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setIdUser(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(userId);
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getIdUser());
    }

    @Test
    void testGetUserByEmail() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByEmail(email);
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
    }

    @Test
    void testCreateUser() {
        String email = "newuser@example.com";
        String password = "password";
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(securityConfig.passwordEncoder().encode(password)).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);
        assertEquals(email, result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
    }

    @Test
    void testCreateUserWithExistingEmail() {
        String email = "existing@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
        assertEquals("The email is already used. {}", exception.getMessage());
    }

    @Test
    void testUpdateUserWithoutPassword() {
        UUID userId = UUID.randomUUID();
        String email = "update@example.com";
        User existingUser = new User();
        existingUser.setIdUser(userId);
        existingUser.setEmail(email);
        existingUser.setPassword("existingPassword");

        User updatedUser = new User();
        updatedUser.setIdUser(userId);
        updatedUser.setEmail(email);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        User result = userService.updateUserWithoutPassword(updatedUser);
        assertEquals(email, result.getEmail());
        assertEquals("existingPassword", result.getPassword());
    }
}
