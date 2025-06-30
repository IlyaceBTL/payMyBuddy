package com.paymybuddy.paymybuddy.service;

import com.paymybuddy.paymybuddy.dto.ProfileDto;
import com.paymybuddy.paymybuddy.model.User;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@Service
public class DashBoardService {

    private static final Logger logger = LogManager.getLogger(DashBoardService.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public DashBoardService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> getUserByEmail(String email) {
        logger.debug("Fetching user by email: {}", email);
        return userService.getUserByEmail(email);
    }

    public ProfileDto createProfileDto(User user) {
        logger.debug("Creating ProfileDto for user: {}", user.getEmail());
        return ProfileDto.fromUser(user);
    }

    public String editProfile(@Valid ProfileDto profileDto,
                              BindingResult bindingResult,
                              UserDetails userDetails,
                              Model model) {
        logger.info("Profile update process started for user '{}'.", userDetails.getUsername());
        Optional<User> userOptional = userService.getUserByEmail(userDetails.getUsername());
        if (userOptional.isEmpty()) {
            logger.error("User not found for email '{}'.", userDetails.getUsername());
            model.addAttribute("errorMessage", "User not found");
            return "profile";
        }

        User user = userOptional.get();

        // Password verification
        if (!passwordEncoder.matches(profileDto.getPassword(), user.getPassword())) {
            logger.warn("Incorrect password provided for user '{}'.", user.getEmail());
            bindingResult.rejectValue("password", "error.password", "Mot de passe incorrect");
        }

        if (bindingResult.hasErrors()) {
            logger.info("Validation errors occurred during profile update for user '{}'.", user.getEmail());
            model.addAttribute("user", user);
            return "profile";
        }

        logger.debug("Updating profile for user '{}'.", user.getEmail());
        user.setFirstName(profileDto.getFirstName());
        user.setLastName(profileDto.getLastName());
        user.setUserName(profileDto.getUserName());
        user.setEmail(profileDto.getEmail());

        userService.updateUserWithoutPassword(user);
        logger.info("Profile successfully updated for user '{}'.", user.getEmail());
        return "redirect:/profile";
    }
}
