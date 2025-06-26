package com.paymybuddy.paymybuddy.service;

import com.paymybuddy.paymybuddy.config.SecurityConfig;
import com.paymybuddy.paymybuddy.model.BankAccount;
import com.paymybuddy.paymybuddy.model.User;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    private final UserService userService;
    private final SecurityConfig securityConfig;
    private final BankAccountService bankAccountService;

    public RegisterService(UserService userService, SecurityConfig securityConfig, BankAccountService bankAccountService) {
        this.userService = userService;
        this.securityConfig = securityConfig;
        this.bankAccountService = bankAccountService;
    }

    public User registerUser(String email, String password, String confirmPassword, String firstName, String lastName , String userName) {
        // Logic to register a new use
        // This could involve saving the user to a database, sending a confirmation email, etc.
        // For now, we will just print the details to the console
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if(email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if(firstName == null || firstName.isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (lastName == null || lastName.isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        BankAccount bankAccount = bankAccountService.createBankAccount();
        User newUser = new User(userName, lastName, firstName, email, password, bankAccount);
        return (userService.createUser(newUser));
    }
}
