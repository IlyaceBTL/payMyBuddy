package com.paymybuddy.paymybuddy.controller;

import com.paymybuddy.paymybuddy.service.RegisterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public String processRegistration(@RequestParam String userName,
                                      @RequestParam String lastName,
                                      @RequestParam String firstName,
                                      @RequestParam String email,
                                      @RequestParam String password,
                                      @RequestParam String confirmPassword) {
        registerService.registerUser(email, password, confirmPassword, firstName, lastName, userName);
        return "redirect:/login";
    }
}
