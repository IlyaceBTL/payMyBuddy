package com.paymybuddy.paymybuddy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bank_account")
public class BankAccount {

    @Id
    @Column(name = "idBankAccount")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBankAccount;

    @NotNull
    @Column(name = "balance", precision = 10, scale = 2, nullable = false)
    private BigDecimal balance;

    @OneToMany(mappedBy = "bankAccount")
    private Set<User> users = new HashSet<>();
}