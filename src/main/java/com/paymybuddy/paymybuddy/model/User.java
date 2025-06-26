package com.paymybuddy.paymybuddy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Id
    @Column(name = "idUser")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUser;

    @NotBlank
    @Column(name = "userName", nullable = false)
    private String userName;

    @NotBlank
    @Column(name = "lastName", nullable = false)
    private String lastName;

    @NotBlank
    @Column(name = "firstName", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "idBank_Account", referencedColumnName = "idBankAccount", nullable = false,
            foreignKey = @ForeignKey(name = "fk_User_Bank_Account"))
    private BankAccount bankAccount;

    public User(String userName, String lastName, String firstName, String email, String password, BankAccount bankAccount) {
        this.userName = userName;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.bankAccount = bankAccount;
    }
}