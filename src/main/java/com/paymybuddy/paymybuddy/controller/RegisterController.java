package com.paymybuddy.paymybuddy.controller;

import com.paymybuddy.paymybuddy.dto.RegisterRequest;
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

    @GetMapping
    public String showRegistrationPage() {
        log.info("Displaying registration page.");
        return "register";
    }

    @PostMapping
    public String processRegistration(@Valid RegisterRequest registerRequest, BindingResult bindingResult, Model model) {
        log.info("Processing registration for email: {}", registerRequest.getEmail());
        if (bindingResult.hasErrors()) {
            log.warn("Registration failed due to validation error: {}", bindingResult.getAllErrors().getFirst().getDefaultMessage());
            model.addAttribute("errorMessage", bindingResult.getAllErrors().getFirst().getDefaultMessage());
            return "register";
        }
        registerService.registerUser(registerRequest.getEmail(), registerRequest.getPassword(), registerRequest.getConfirmPassword(), registerRequest.getFirstName(), registerRequest.getLastName(), registerRequest.getUserName());
        log.info("Registration successful for email: {}", registerRequest.getEmail());
        return "redirect:/login";
    }
}
