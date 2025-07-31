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

    private static final String ERROR_MESSAGE = "errorMessage";

    public DashBoardController(DashBoardService dashBoardService) {
        this.dashBoardService = dashBoardService;
    }

    /**
     * Display the dashboard page with user's contacts and transactions.
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("User accessed the dashboard page.");
        // Add contacts and transactions to the model
        model.addAttribute("contacts", dashBoardService.getContactsForUser(userDetails.getUsername()));
        model.addAttribute("transactions", dashBoardService.getTransactionsForUser(userDetails.getUsername()));
        return "dashboard";
    }

    /**
     * Display the profile page for the authenticated user.
     */
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
            model.addAttribute(ERROR_MESSAGE, "User not found");
        }
        return "profile";
    }

    /**
     * Handle profile edit form submission.
     */
    @PostMapping("/profile/edit")
    public String editProfile(@Valid @ModelAttribute("profileDto") ProfileDto profileDto,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        logger.info("User '{}' attempts to edit their profile.", userDetails.getUsername());
        return dashBoardService.editProfile(profileDto, bindingResult, userDetails, model);
    }

    /**
     * Display the add relation (friend) page.
     */
    @GetMapping("/relation")
    public String showAddRelation() {
        logger.info("User accessed the add relation page.");
        return "relation";
    }

    /**
     * Handle the submission of the add relation (friend) form.
     */
    @PostMapping("/relation/add")
    public String addRelation(@RequestParam("friendEmail") String friendEmail,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        logger.info("User '{}' attempts to add friend '{}'.", userDetails.getUsername(), friendEmail);
        boolean result = dashBoardService.addFriendByEmail(userDetails.getUsername(), friendEmail);
        if (result) {
            redirectAttributes.addFlashAttribute("successMessage", "Relation ajoutée avec succès.");
            logger.info("Friend '{}' added for user '{}'.", friendEmail, userDetails.getUsername());
        } else {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Impossible d'ajouter la relation. Vérifiez que l'email existe ou n'est pas déjà un ami.");
            logger.warn("Failed to add friend '{}' for user '{}'.", friendEmail, userDetails.getUsername());
        }
        return "redirect:/relation";
    }

    /**
     * Handle the submission of the money transfer form.
     */
    @PostMapping("/transfer")
    public String transferMoney(@RequestParam("contactEmail") String contactEmail,
                                @RequestParam("amount") double amount,
                                @RequestParam("description") String description,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        logger.info("User '{}' attempts to transfer {}€ to '{}' with description '{}'.", userDetails.getUsername(), amount, contactEmail, description);
        boolean result = dashBoardService.transferMoney(userDetails.getUsername(), contactEmail, amount, description);
        if (result) {
            redirectAttributes.addFlashAttribute("successMessage", "Transfert effectué avec succès.");
            logger.info("Transfer successful from '{}' to '{}'.", userDetails.getUsername(), contactEmail);
        } else {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Échec du transfert. Veuillez vérifier votre solde ou le contact.");
            logger.warn("Transfer failed from '{}' to '{}'.", userDetails.getUsername(), contactEmail);
        }
        return "redirect:/dashboard";
    }
}
