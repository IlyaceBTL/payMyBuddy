package com.paymybuddy.paymybuddy.controller;


import com.paymybuddy.paymybuddy.service.LoginService;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class LoginController {
    Logger log = org.apache.logging.log4j.LogManager.getLogger(LoginController.class);


    private  final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        log.info("Displaying login page");
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password) {
        try {
            loginService.login(email, password);
            return "redirect:/dashboard";
        } catch (IllegalArgumentException e) {
            log.error("Error : Email ou mot de passe incorrect");
            return "login";
        }
    }
}
