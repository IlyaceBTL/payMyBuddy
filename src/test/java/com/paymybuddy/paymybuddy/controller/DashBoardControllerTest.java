package com.paymybuddy.paymybuddy.controller;

import com.paymybuddy.paymybuddy.dto.ProfileDto;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.service.DashBoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashBoardControllerTest {

    private DashBoardService dashBoardService;
    private DashBoardController controller;

    @BeforeEach
    void setUp() {
        dashBoardService = mock(DashBoardService.class);
        controller = new DashBoardController(dashBoardService);
    }

    @Test
    void showDashboard_shouldAddContactsAndTransactionsAndReturnDashboard() {
        Model model = mock(Model.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user@mail.com");

        when(dashBoardService.getContactsForUser("user@mail.com")).thenReturn(java.util.Collections.emptyList());
        when(dashBoardService.getTransactionsForUser("user@mail.com")).thenReturn(java.util.Collections.emptyList());

        String view = controller.showDashboard(model, userDetails);

        assertEquals("dashboard", view);
        verify(model).addAttribute(eq("contacts"), any());
        verify(model).addAttribute(eq("transactions"), any());
    }

    @Test
    void showProfile_shouldAddUserAndProfileDto_whenUserFound() {
        Model model = mock(Model.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user@mail.com");
        User user = new User();
        ProfileDto profileDto = new ProfileDto();

        when(dashBoardService.getUserByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(dashBoardService.createProfileDto(user)).thenReturn(profileDto);

        String view = controller.showProfile(model, userDetails);

        assertEquals("profile", view);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("profileDto", profileDto);
    }

    @Test
    void showProfile_shouldAddErrorMessage_whenUserNotFound() {
        Model model = mock(Model.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("notfound@mail.com");

        when(dashBoardService.getUserByEmail("notfound@mail.com")).thenReturn(Optional.empty());

        String view = controller.showProfile(model, userDetails);

        assertEquals("profile", view);
        verify(model).addAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void editProfile_shouldDelegateToService() {
        ProfileDto profileDto = mock(ProfileDto.class);
        BindingResult bindingResult = mock(BindingResult.class);
        UserDetails userDetails = mock(UserDetails.class);
        Model model = mock(Model.class);

        when(dashBoardService.editProfile(profileDto, bindingResult, userDetails, model)).thenReturn("resultView");

        String view = controller.editProfile(profileDto, bindingResult, userDetails, model);

        assertEquals("resultView", view);
        verify(dashBoardService).editProfile(profileDto, bindingResult, userDetails, model);
    }

    @Test
    void showAddRelation_shouldReturnRelation() {
        String view = controller.showAddRelation();
        assertEquals("relation", view);
    }

    @Test
    void addRelation_shouldRedirectAndAddSuccessMessage_whenSuccess() {
        UserDetails userDetails = mock(UserDetails.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        when(userDetails.getUsername()).thenReturn("user@mail.com");
        when(dashBoardService.addFriendByEmail("user@mail.com", "friend@mail.com")).thenReturn(true);

        String view = controller.addRelation("friend@mail.com", userDetails, redirectAttributes);

        assertEquals("redirect:/relation", view);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void addRelation_shouldRedirectAndAddErrorMessage_whenFail() {
        UserDetails userDetails = mock(UserDetails.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        when(userDetails.getUsername()).thenReturn("user@mail.com");
        when(dashBoardService.addFriendByEmail("user@mail.com", "friend@mail.com")).thenReturn(false);

        String view = controller.addRelation("friend@mail.com", userDetails, redirectAttributes);

        assertEquals("redirect:/relation", view);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void transferMoney_shouldRedirectAndAddSuccessMessage_whenSuccess() {
        UserDetails userDetails = mock(UserDetails.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        when(userDetails.getUsername()).thenReturn("user@mail.com");
        when(dashBoardService.transferMoney("user@mail.com", "friend@mail.com", 10.0, "desc")).thenReturn(true);

        String view = controller.transferMoney("friend@mail.com", 10.0, "desc", userDetails, redirectAttributes);

        assertEquals("redirect:/dashboard", view);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void transferMoney_shouldRedirectAndAddErrorMessage_whenFail() {
        UserDetails userDetails = mock(UserDetails.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        when(userDetails.getUsername()).thenReturn("user@mail.com");
        when(dashBoardService.transferMoney("user@mail.com", "friend@mail.com", 10.0, "desc")).thenReturn(false);

        String view = controller.transferMoney("friend@mail.com", 10.0, "desc", userDetails, redirectAttributes);

        assertEquals("redirect:/dashboard", view);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }
}

