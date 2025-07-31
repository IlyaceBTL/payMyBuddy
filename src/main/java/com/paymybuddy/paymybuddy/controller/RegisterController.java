package com.paymybuddy.paymybuddy.controller;

import com.paymybuddy.paymybuddy.dto.RegisterRequestDto;
import com.paymybuddy.paymybuddy.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegisterController {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(RegisterController.class);

    private final RegisterService registerService;

    private static final String REGISTER ="register";

    /**
     * Display the registration page.
     * If the model does not already contain a "user" attribute, add a new RegisterRequestDto.
     * @param model the Spring MVC model
     * @return the registration view name
     */
    @GetMapping
    public String showRegistrationPage(Model model) {
        log.info("Displaying registration page.");
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new RegisterRequestDto());
        }
        return REGISTER;
    }

    /**
     * Handle the registration form submission.
     * If there are validation errors, return to the registration page with an error message.
     * If registration succeeds, redirect to the login page.
     * If registration fails due to a service exception, return to the registration page with an error message.
     * @param registerRequestDto the registration form data
     * @param bindingResult validation result
     * @param model the Spring MVC model
     * @return the next view name
     */
    @PostMapping
    public String processRegistration(@Valid RegisterRequestDto registerRequestDto, BindingResult bindingResult, Model model) {
        log.info("Processing registration for email: {}", registerRequestDto.getEmail());
        if (bindingResult.hasErrors()) {
            log.warn("Registration failed due to validation error: {}", bindingResult.getAllErrors().getFirst().getDefaultMessage());
            // Clear passwords for security
            registerRequestDto.setPassword("");
            registerRequestDto.setConfirmPassword("");
            model.addAttribute("user", registerRequestDto);
            model.addAttribute("errorMessage", "Erreur d'inscription : " + bindingResult.getAllErrors().getFirst().getDefaultMessage());
            return REGISTER;
        }
        try {
            // Attempt to register the user
            registerService.registerUser(
                registerRequestDto.getEmail(),
                registerRequestDto.getPassword(),
                registerRequestDto.getConfirmPassword(),
                registerRequestDto.getFirstName(),
                registerRequestDto.getLastName(),
                registerRequestDto.getUserName()
            );
            log.info("Registration successful for email: {}", registerRequestDto.getEmail());
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            log.warn("Registration failed: {}", ex.getMessage());
            // Clear passwords for security
            registerRequestDto.setPassword("");
            registerRequestDto.setConfirmPassword("");
            model.addAttribute("user", registerRequestDto);
            model.addAttribute("errorMessage", "Erreur lors de l'inscription : " + ex.getMessage());
            return REGISTER;
        }
    }
}
