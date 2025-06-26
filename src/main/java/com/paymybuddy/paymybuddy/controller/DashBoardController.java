package com.paymybuddy.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashBoardController {
    // This controller will handle the dashboard-related requests
    // Currently, it does not have any methods, but you can add them as needed

    // Example method to show the dashboard page
     @GetMapping("/dashboard")
     public String showDashboard() {
         return "dashboard"; // Return the name of the dashboard view
     }
}
