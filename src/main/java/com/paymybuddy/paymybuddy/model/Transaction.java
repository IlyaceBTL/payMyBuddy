package com.paymybuddy.paymybuddy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a transaction between two users.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {

    /**
     * Unique identifier for the transaction.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaction")
    private Long idTransaction;

    /**
     * The amount of the transaction.
     */
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    /**
     * The date and time of the transaction.
     */
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    /**
     * The description of the transaction.
     */
    @Column(name = "description", nullable = false, length = 255)
    private String description;

    /**
     * The user who sent the money.
     */
    @ManyToOne
    @JoinColumn(name = "id_user_sender", referencedColumnName = "id_user", nullable = false)
    private User userSender;

    /**
     * The user who received the money.
     */
    @ManyToOne
    @JoinColumn(name = "id_user_receveir", referencedColumnName = "id_user", nullable = false)
    private User userReceveir;

    /**
     * The transaction fee applied.
     */
    @Column(name = "fee", nullable = false)
    private BigDecimal fee;
}
