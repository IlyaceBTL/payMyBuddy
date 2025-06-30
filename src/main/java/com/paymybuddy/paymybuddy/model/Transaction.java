package com.paymybuddy.paymybuddy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTransaction")
    private Long idTransaction;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @ManyToOne
    @JoinColumn(name = "idUserSender", referencedColumnName = "idUser", nullable = false)
    private User userSender;

    @ManyToOne
    @JoinColumn(name = "idUserReceveir", referencedColumnName = "idUser", nullable = false)
    private User userReceveir;

    @Column(name = "fee", nullable = false)
    private BigDecimal fee;

}
