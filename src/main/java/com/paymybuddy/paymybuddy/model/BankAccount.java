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
@Table(name = "bank_account", uniqueConstraints = {
        @UniqueConstraint(name = "idBank_Account_UNIQUE", columnNames = "idBank_Account")
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