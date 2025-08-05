package com.paymybuddy.paymybuddy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a bank account.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bank_account")
public class BankAccount {

    /**
     * Unique identifier for the bank account.
     */
    @Id
    @Column(name = "id_bank_account")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBankAccount;

    /**
     * The balance of the bank account.
     */
    @NotNull
    @Column(name = "balance", precision = 10, scale = 2, nullable = false)
    private BigDecimal balance;

    /**
     * The set of users associated with this bank account.
     */
    @OneToMany(mappedBy = "bankAccount")
    private Set<User> users = new HashSet<>();
}