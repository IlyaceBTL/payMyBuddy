package com.paymybuddy.paymybuddy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(name = "idUser_UNIQUE", columnNames = "idUser"),
        @UniqueConstraint(name = "email_UNIQUE", columnNames = "email")
})
public class User {

    /**
     * Unique identifier for the user (UUID).
     */
    @Id
    @Column(name = "idUser", columnDefinition = "BINARY(16)")
    private UUID idUser;

    /**
     * Username of the user.
     */
    @NotBlank
    @Column(name = "userName", nullable = false)
    private String userName;

    /**
     * Last name of the user.
     */
    @NotBlank
    @Column(name = "lastName", nullable = false)
    private String lastName;

    /**
     * First name of the user.
     */
    @NotBlank
    @Column(name = "firstName", nullable = false)
    private String firstName;

    /**
     * Email address of the user (must be unique).
     */
    @NotBlank
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Encrypted password of the user.
     */
    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Reference to the user's bank account.
     */
    @ManyToOne
    @JoinColumn(name = "idBankAccount", referencedColumnName = "idBankAccount", nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_bank_account"))
    private BankAccount bankAccount;

    /**
     * Constructor without idUser, used for creating new users.
     */
    public User(String userName, String lastName, String firstName, String email, String password, BankAccount bankAccount) {
        this.userName = userName;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.bankAccount = bankAccount;
    }

    /**
     * Generates a random UUID for the user before persisting if not already set.
     */
    @PrePersist
    public void generateId() {
        if (idUser == null) {
            idUser = UUID.randomUUID();
        }
    }
}