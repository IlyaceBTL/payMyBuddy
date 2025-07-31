package com.paymybuddy.paymybuddy.dto;

import com.paymybuddy.paymybuddy.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
public class ProfileDto {

    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    private String userName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    private String email;

    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;

    private String confirmPassword;

    public static ProfileDto fromUser(User user) {
        ProfileDto dto = new ProfileDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setPassword("");
        dto.setConfirmPassword("");
        return dto;
    }

    public boolean isPasswordConfirmed() {
        if (!StringUtils.hasText(password)) {
            return true; // Pas de changement de mot de passe
        }
        return password.equals(confirmPassword);
    }
}
