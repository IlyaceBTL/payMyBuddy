package com.paymybuddy.paymybuddy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Bank_Account", uniqueConstraints = {
        @UniqueConstraint(name = "idBankAccount", columnNames = "idBankAccount")
})
public class BankAccount {

    @Id
    @Column(name = "idBankAccount")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idBankAccount;

    @NotNull
    @Column(name = "balance", precision = 10, scale = 2, nullable = false)
    private BigDecimal balance;
}