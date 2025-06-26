package com.paymybuddy.paymybuddy.service;

import com.paymybuddy.paymybuddy.config.SecurityConfig;
import com.paymybuddy.paymybuddy.model.User;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final Logger Log = org.apache.logging.log4j.LogManager.getLogger(LoginService.class);
    private final UserService userService;

    private final SecurityConfig securityConfig;

    public LoginService(UserService userService, SecurityConfig securityConfig) {
        this.userService = userService;
        this.securityConfig = securityConfig;
    }

    public void login(String email, String password) {
        Log.info("Attempting to log in user: {}", email);
        Optional<User> user = userService.getUserByEmail(email);
        if (user.isPresent() && securityConfig.passwordEncoder().matches(password, user.get().getPassword())) {
            Log.info("User {} logged in successfully", email);
        } else {
            Log.error("Login failed for user: {}", email);
            throw new IllegalArgumentException("Invalid username or password");
        }
    }
}
