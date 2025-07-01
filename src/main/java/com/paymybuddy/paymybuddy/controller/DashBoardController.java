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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class DashBoardController {

    private static final Logger logger = LogManager.getLogger(DashBoardController.class);

    private final DashBoardService dashBoardService;

    public DashBoardController(DashBoardService dashBoardService) {
        this.dashBoardService = dashBoardService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("User accessed the dashboard page.");
        // Ajoute les contacts et transactions au modèle
        model.addAttribute("contacts", dashBoardService.getContactsForUser(userDetails.getUsername()));
        model.addAttribute("transactions", dashBoardService.getTransactionsForUser(userDetails.getUsername()));
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

    @PostMapping("/relation/add")
    public String addRelation(@RequestParam("friendEmail") String friendEmail,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        logger.info("User '{}' attempts to add friend '{}'.", userDetails.getUsername(), friendEmail);
        boolean result = dashBoardService.addFriendByEmail(userDetails.getUsername(), friendEmail);
        if (result) {
            redirectAttributes.addFlashAttribute("successMessage", "Friend added successfully.");
            logger.info("Friend '{}' added for user '{}'.", friendEmail, userDetails.getUsername());
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to add friend. Check if the email exists or is already a friend.");
            logger.warn("Failed to add friend '{}' for user '{}'.", friendEmail, userDetails.getUsername());
        }
        return "redirect:/relation";
    }

    @PostMapping("/transfer")
    public String transferMoney(@RequestParam("contactEmail") String contactEmail,
                                @RequestParam("amount") double amount,
                                @RequestParam("description") String description,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        logger.info("User '{}' attempts to transfer {}€ to '{}' with description '{}'.", userDetails.getUsername(), amount, contactEmail, description);
        boolean result = dashBoardService.transferMoney(userDetails.getUsername(), contactEmail, amount, description);
        if (result) {
            redirectAttributes.addFlashAttribute("successMessage", "Transfer successful.");
            logger.info("Transfer successful from '{}' to '{}'.", userDetails.getUsername(), contactEmail);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Transfer failed. Please check your balance or contact.");
            logger.warn("Transfer failed from '{}' to '{}'.", userDetails.getUsername(), contactEmail);
        }
        return "redirect:/dashboard";
    }
}
