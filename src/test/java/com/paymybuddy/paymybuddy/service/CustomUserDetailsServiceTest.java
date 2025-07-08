package com.paymybuddy.paymybuddy.service;

import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private UserRepository userRepository;
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        String email = "test@test.com";
        String password = "password123";
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowException() {
        String email = "nonexistent@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class, () ->
            userDetailsService.loadUserByUsername(email)
        );

        assertEquals(email, exception.getMessage());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void loadUserByUsername_WhenEmailIsNull_ShouldThrowException() {
        assertThrows(UsernameNotFoundException.class, () ->
            userDetailsService.loadUserByUsername(null)
        );
    }
}

