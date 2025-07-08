package com.paymybuddy.paymybuddy.service;

import com.paymybuddy.paymybuddy.model.BankAccount;
import com.paymybuddy.paymybuddy.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisterServiceTest {

    private UserService userService;
    private BankAccountService bankAccountService;
    private RegisterService registerService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        bankAccountService = mock(BankAccountService.class);
        registerService = new RegisterService(userService, bankAccountService);
    }

    @Test
    void registerUser_shouldCreateUser_whenAllIsValid() {
        String email = "test@mail.com";
        String password = "pass";
        String confirmPassword = "pass";
        String firstName = "John";
        String lastName = "Doe";
        String userName = "johndoe";

        when(userService.isInvalidEmail(email)).thenReturn(false);
        when(userService.getUserByEmail(email)).thenReturn(Optional.empty());
        BankAccount bankAccount = new BankAccount();
        when(bankAccountService.createBankAccount()).thenReturn(bankAccount);
        User user = new User(userName, lastName, firstName, email, password, bankAccount);
        when(userService.createUser(any(User.class))).thenReturn(user);

        User result = registerService.registerUser(email, password, confirmPassword, firstName, lastName, userName);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userService).createUser(any(User.class));
    }

    @Test
    void registerUser_shouldThrow_whenPasswordsDoNotMatch() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                registerService.registerUser("a@b.com", "pass1", "pass2", "John", "Doe", "johndoe"));
        assertEquals("Passwords do not match", ex.getMessage());
    }

    @Test
    void registerUser_shouldThrow_whenEmailIsEmpty() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                registerService.registerUser("", "pass", "pass", "John", "Doe", "johndoe"));
        assertEquals("Email cannot be empty", ex.getMessage());
    }

    @Test
    void registerUser_shouldThrow_whenFirstNameIsEmpty() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                registerService.registerUser("a@b.com", "pass", "pass", "", "Doe", "johndoe"));
        assertEquals("First name cannot be empty", ex.getMessage());
    }

    @Test
    void registerUser_shouldThrow_whenLastNameIsEmpty() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                registerService.registerUser("a@b.com", "pass", "pass", "John", "", "johndoe"));
        assertEquals("Last name cannot be empty", ex.getMessage());
    }

    @Test
    void registerUser_shouldThrow_whenEmailIsInvalid() {
        when(userService.isInvalidEmail("badmail")).thenReturn(true);
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                registerService.registerUser("badmail", "pass", "pass", "John", "Doe", "johndoe"));
        assertTrue(ex.getMessage().contains("invalid"));
    }

    @Test
    void registerUser_shouldThrow_whenEmailAlreadyUsed() {
        when(userService.isInvalidEmail("a@b.com")).thenReturn(false);
        when(userService.getUserByEmail("a@b.com")).thenReturn(Optional.of(mock(User.class)));
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                registerService.registerUser("a@b.com", "pass", "pass", "John", "Doe", "johndoe"));
        assertTrue(ex.getMessage().contains("already used"));
    }
}

