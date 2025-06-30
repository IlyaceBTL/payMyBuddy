package com.paymybuddy.paymybuddy.controller;

import com.paymybuddy.paymybuddy.dto.ProfileDto;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.service.DashBoardService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class DashBoardController {

    private static final Logger logger = LogManager.getLogger(DashBoardController.class);

    private final DashBoardService dashBoardService;

    public DashBoardController(DashBoardService dashBoardService) {
        this.dashBoardService = dashBoardService;
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        logger.info("User accessed the dashboard page.");
        return "dashboard";
    }

    @GetMapping("/profile")
    public String showProfile(Model model,
                              @AuthenticationPrincipal UserDetails user) {
        logger.info("User '{}' accessed the profile page.", user.getUsername());
        Optional<User> userOptional = dashBoardService.getUserByEmail(user.getUsername());
        if (userOptional.isPresent()) {
            logger.debug("Profile data loaded for user '{}'.", user.getUsername());
            model.addAttribute("user", userOptional.get());
            model.addAttribute("profileDto", dashBoardService.createProfileDto(userOptional.get()));
        } else {
            logger.warn("User not found for email '{}'.", user.getUsername());
            model.addAttribute("errorMessage", "User not found");
        }
        return "profile";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@Valid @ModelAttribute("profileDto") ProfileDto profileDto,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        logger.info("User '{}' attempts to edit their profile.", userDetails.getUsername());
        return dashBoardService.editProfile(profileDto, bindingResult, userDetails, model);
    }

    @GetMapping("/relation")
    public String showAddRelation() {
        logger.info("User accessed the add relation page.");
        return "relation";
    }
}
