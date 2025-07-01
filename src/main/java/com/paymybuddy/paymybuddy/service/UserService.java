package com.paymybuddy.paymybuddy.service;

import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.UserRepository;
import com.paymybuddy.paymybuddy.config.SecurityConfig;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.paymybuddy.paymybuddy.logger.LogMessages.*;

@Service
public class UserService {

    private static final Logger Log = org.apache.logging.log4j.LogManager.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final SecurityConfig securityConfig;

    public UserService(UserRepository userRepository, SecurityConfig securityConfig) {
        this.userRepository = userRepository;
        this.securityConfig = securityConfig;
    }

    public Optional<User> getUserById(UUID userId) {
        Log.debug("Fetching user by id: {}", userId);
        return userRepository.findById(userId);
    }

    public Optional<User> getUserByUserName(String userName) {
        Log.debug("Fetching user by username: {}", userName);
        return userRepository.findByUserName(userName);
    }

    public Optional<User> getUserByEmail(String email) {
        Log.debug("Fetching user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        Log.info("Saving user with email: {}", user.getEmail());
        return userRepository.save(user);
    }

    public void deleteUser(UUID userId) {
        Log.info("Deleting user with id: {}", userId);
        userRepository.deleteById(userId);
    }

    public User updateUser(User user) {
        String email = user.getEmail();
        if (isInvalidEmail(email)) {
            Log.error(INVALID_EMAIL, email);
            throw new IllegalArgumentException(INVALID_EMAIL);
        }
        Log.info("Updating user (with password) for email: {}", email);
        user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUserWithoutPassword(User user) {
        String email = user.getEmail();
        if (isInvalidEmail(email)) {
            Log.error(INVALID_EMAIL, email);
            throw new IllegalArgumentException(INVALID_EMAIL);
        }
        Log.info("Updating user (without password) for email: {}", email);
        Optional<User> existing = userRepository.findById(user.getIdUser());
        existing.ifPresent(u -> user.setPassword(u.getPassword()));
        return userRepository.save(user);
    }

    public User createUser(User user) {
        String email = user.getEmail();
        if (isInvalidEmail(email)) {
            Log.error(INVALID_EMAIL, email);
            throw new IllegalArgumentException(INVALID_EMAIL);
        }
        if (userRepository.findByEmail(email).isPresent()) {
            Log.error(EMAIL_ALREADY_USED, email);
            throw new IllegalArgumentException(EMAIL_ALREADY_USED);
        }
        Log.info("Creating new user with email: {}", email);
        user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));
        return userRepository.save(user);
    }

    public boolean isInvalidEmail(String email) {
        // Simple regex for email validation
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email == null || !email.matches(emailRegex);
    }
}
