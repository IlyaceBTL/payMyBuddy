package com.paymybuddy.paymybuddy.controller;

import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    Logger log = org.apache.logging.log4j.LogManager.getLogger(LoginController.class);

    @GetMapping("/login")
    public String showLoginPage() {
        log.info("Displaying login page");
        return "login";
    }
}
