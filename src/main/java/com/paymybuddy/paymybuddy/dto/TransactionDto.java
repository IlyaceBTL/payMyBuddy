package com.paymybuddy.paymybuddy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TransactionDto {
    public String contactName;
    public String description;
    public BigDecimal amount;
}
