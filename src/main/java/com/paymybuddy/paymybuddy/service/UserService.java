package com.paymybuddy.paymybuddy.service;

import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.repository.UserRepository;
import com.paymybuddy.paymybuddy.config.SecurityConfig;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> getUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public User updateUser(User user) {
        String email = user.getEmail();
        if (isInvalidEmail(email)) {
            Log.error(INVALIDE_EMAIL, email);
            throw new IllegalArgumentException(INVALIDE_EMAIL);
        }
        user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User createUser(User user) {
        String email = user.getEmail();
        if (isInvalidEmail(email)) {
            Log.error(INVALIDE_EMAIL, email);
            throw new IllegalArgumentException(INVALIDE_EMAIL);
        }
        if (userRepository.findByEmail(email).isPresent()) {
            Log.error(INVALIDE_EMAIL, email);
            throw new IllegalArgumentException(INVALIDE_EMAIL);
        }
        user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));
        return userRepository.save(user);
    }

    private boolean isInvalidEmail(String email) {
        // Simple regex for email validation
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email == null || !email.matches(emailRegex);
    }



}
