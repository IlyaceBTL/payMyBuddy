package com.paymybuddy.paymybuddy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Data Transfer Object representing a transaction between users.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TransactionDto {
    /**
     * The name of the contact involved in the transaction.
     */
    private String contactName;

    /**
     * The description of the transaction.
     */
    private String description;

    /**
     * The amount of the transaction.
     */
    private BigDecimal amount;
}
