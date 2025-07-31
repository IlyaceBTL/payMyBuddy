package com.paymybuddy.paymybuddy.controller;

import com.paymybuddy.paymybuddy.dto.RegisterRequestDto;
import com.paymybuddy.paymybuddy.service.RegisterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisterControllerTest {

    private RegisterService registerService;
    private RegisterController registerController;
    private Model model;
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        registerService = mock(RegisterService.class);
        registerController = new RegisterController(registerService);
        model = mock(Model.class);
        bindingResult = mock(BindingResult.class);
    }

    @Test
    void showRegistrationPage_shouldReturnRegisterView() {
        when(model.containsAttribute("user")).thenReturn(false);
        String view = registerController.showRegistrationPage(model);
        assertEquals("register", view);
    }

    @Test
    void processRegistration_shouldRedirectToLogin_onSuccess() {
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setEmail("test@mail.com");
        dto.setPassword("pass");
        dto.setConfirmPassword("pass");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setUserName("johndoe");

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = registerController.processRegistration(dto, bindingResult, model);

        assertEquals("redirect:/login", view);
        verify(registerService).registerUser(
                "test@mail.com",
                "pass",
                "pass",
                "John",
                "Doe",
                "johndoe");
    }

    @Test
    void processRegistration_shouldReturnRegisterView_onValidationError() {
        RegisterRequestDto dto = new RegisterRequestDto();
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(java.util.List.of(
                new org.springframework.validation.ObjectError("user", "Validation error")
        ));

        String view = registerController.processRegistration(dto, bindingResult, model);

        assertEquals("register", view);
        verify(model).addAttribute("user", dto);
        verify(model).addAttribute("errorMessage", "Erreur d'inscription : Validation error");
        assertEquals("", dto.getPassword());
        assertEquals("", dto.getConfirmPassword());
    }

    @Test
    void processRegistration_shouldReturnRegisterView_onServiceException() {
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setEmail("test@mail.com");
        dto.setPassword("pass");
        dto.setConfirmPassword("pass");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setUserName("johndoe");

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new IllegalArgumentException("Email already exists")).when(registerService)
                .registerUser(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());

        String view = registerController.processRegistration(dto, bindingResult, model);

        assertEquals("register", view);
        verify(model).addAttribute("user", dto);
        verify(model).addAttribute("errorMessage", "Erreur lors de l'inscription : Email already exists");
        assertEquals("", dto.getPassword());
        assertEquals("", dto.getConfirmPassword());
    }
}
