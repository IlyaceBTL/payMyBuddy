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

    /**
     * Create a new bank account with a zero balance and save it.
     * @return the created BankAccount
     */
    public BankAccount createBankAccount() {
        log.info("Creating a new bank account");
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(BigDecimal.ZERO);
        return saveBankAccount(bankAccount);
    }

    /**
     * Save the given bank account to the repository.
     * @param bankAccount the bank account to save
     * @return the saved BankAccount
     */
    public BankAccount saveBankAccount(BankAccount bankAccount) {
        log.info("Saving bank account: {}", bankAccount);
        return bankAccountRepository.save(bankAccount);
    }
}
