package com.paymybuddy.paymybuddy.service;

import com.paymybuddy.paymybuddy.logger.LogMessages;
import com.paymybuddy.paymybuddy.model.BankAccount;
import com.paymybuddy.paymybuddy.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class RegisterService {

    private static final Logger log = LogManager.getLogger(RegisterService.class);


    private final UserService userService;
    private final BankAccountService bankAccountService;

    public RegisterService(UserService userService, BankAccountService bankAccountService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }

    @Transactional
    public User registerUser(String email, String password, String confirmPassword, String firstName, String lastName, String userName) {
        log.info("Starting registration for email: {}", email);

        if (!password.equals(confirmPassword)) {
            log.error(LogMessages.PASSWORDS_DO_NOT_MATCH, email);
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (email == null || email.isEmpty()) {
            log.error(LogMessages.EMPTY_EMAIL);
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (firstName == null || firstName.isEmpty()) {
            log.error(LogMessages.EMPTY_FIRSTNAME, email);
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (lastName == null || lastName.isEmpty()) {
            log.error(LogMessages.EMPTY_LASTNAME, email);
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (userService.isInvalidEmail(email)) {
            log.error(LogMessages.INVALID_EMAIL, email);
            throw new IllegalArgumentException("The email provided is invalid: " + email);
        }
        if (userService.getUserByEmail(email).isPresent()) {
            log.error(LogMessages.EMAIL_ALREADY_USED, email);
            throw new IllegalArgumentException("The email is already used: " + email);
        }

        BankAccount bankAccount = bankAccountService.createBankAccount();
        log.info("Bank account created for email: {}", email);

        User newUser = new User(userName, lastName, firstName, email, password, bankAccount);
        User savedUser = userService.createUser(newUser);
        log.info("User successfully created: {}", email);

        return savedUser;
    }
}
