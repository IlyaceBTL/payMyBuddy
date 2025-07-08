package com.paymybuddy.paymybuddy.service;

import com.paymybuddy.paymybuddy.model.BankAccount;
import com.paymybuddy.paymybuddy.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankAccountServiceTest {

    private BankAccountRepository bankAccountRepository;
    private BankAccountService bankAccountService;

    @BeforeEach
    void setUp() {
        bankAccountRepository = mock(BankAccountRepository.class);
        bankAccountService = new BankAccountService(bankAccountRepository);
    }

    @Test
    void createBankAccount_shouldReturnBankAccountWithZeroBalance() {
        BankAccount saved = new BankAccount();
        saved.setBalance(BigDecimal.ZERO);
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(saved);

        BankAccount result = bankAccountService.createBankAccount();

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getBalance());
        verify(bankAccountRepository).save(any(BankAccount.class));
    }

    @Test
    void saveBankAccount_shouldCallRepository() {
        BankAccount account = new BankAccount();
        when(bankAccountRepository.save(account)).thenReturn(account);

        BankAccount result = bankAccountService.saveBankAccount(account);

        assertEquals(account, result);
        verify(bankAccountRepository).save(account);
    }

}
