package com.paymybuddy.paymybuddy.service;

import com.paymybuddy.paymybuddy.model.BankAccount;
import com.paymybuddy.paymybuddy.repository.BankAccountRepository;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BankAccountService {

    Logger log = org.apache.logging.log4j.LogManager.getLogger(BankAccountService.class);

    private final BankAccountRepository bankAccountRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public BankAccount createBankAccount() {
        log.info("Creating a new bank account");
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(BigDecimal.ZERO);
        return saveBankAccount(bankAccount);
    }

    public BankAccount saveBankAccount(BankAccount bankAccount) {
        log.info("Saving bank account: {}", bankAccount);
        return bankAccountRepository.save(bankAccount);
    }
}
