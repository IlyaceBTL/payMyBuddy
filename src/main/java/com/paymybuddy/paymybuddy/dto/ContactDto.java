package com.paymybuddy.paymybuddy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object representing a contact (friend) of a user.
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ContactDto {
    /**
     * The email address of the contact.
     */
    private String email;

    /**
     * The full name of the contact.
     */
    private String fullName;
}
