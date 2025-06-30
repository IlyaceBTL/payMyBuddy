package com.paymybuddy.paymybuddy.controller;

import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.service.RegisterService;
import com.paymybuddy.paymybuddy.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class DashBoardController {

    private final UserService userService;

    public DashBoardController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/dashboard")
     public String showDashboard() {
         return "dashboard";
     }

     @GetMapping("/profile")
     public String showProfile(Model model,
                               @AuthenticationPrincipal UserDetails user){

         Optional<User> userOptional = userService.getUserByEmail(user.getUsername());
            if (userOptional.isPresent()) {
                model.addAttribute("user", userOptional.get());
            } else {
                model.addAttribute("errorMessage", "User not found");
            }
         return "profile";
     }

     @GetMapping("/relation")
     public String showAddRelation() {
         return "relation";
     }
}
