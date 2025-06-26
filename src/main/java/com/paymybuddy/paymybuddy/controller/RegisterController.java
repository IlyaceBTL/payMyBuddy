package com.paymybuddy.paymybuddy.controller;

import com.paymybuddy.paymybuddy.dto.RegisterRequest;
import com.paymybuddy.paymybuddy.service.RegisterService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.apache.logging.log4j.Logger;

@Controller
public class RegisterController {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(RegisterController.class);


    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @GetMapping("/register")
    public String showRegistrationPage() {
        log.info("Displaying Register page");
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(
            @Valid RegisterRequest registerRequest,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", bindingResult.getAllErrors().getFirst().getDefaultMessage());
            return "register";
        }
        registerService.registerUser(
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getConfirmPassword(),
                registerRequest.getFirstName(),
                registerRequest.getLastName(),
                registerRequest.getUserName()
        );
        return "redirect:/login";
    }
}
