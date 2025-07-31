package com.paymybuddy.paymybuddy.dto;

import com.paymybuddy.paymybuddy.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

/**
 * Data Transfer Object for user profile editing.
 */
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

    /**
     * Creates a ProfileDto from a User entity.
     * @param user the User entity
     * @return a ProfileDto with user data
     */
    public static ProfileDto fromUser(User user) {
        ProfileDto dto = new ProfileDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setPassword(""); // Password fields are left empty for security
        dto.setConfirmPassword("");
        return dto;
    }

    /**
     * Checks if the password and confirmation match.
     * If password is empty, returns true (no password change).
     * @return true if confirmed, false otherwise
     */
    public boolean isPasswordConfirmed() {
        if (!StringUtils.hasText(password)) {
            return true; // No password change
        }
        return password.equals(confirmPassword);
    }
}
