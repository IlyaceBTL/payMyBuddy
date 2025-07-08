package com.paymybuddy.paymybuddy.service;

import com.paymybuddy.paymybuddy.dto.ProfileDto;
import com.paymybuddy.paymybuddy.dto.ContactDto;
import com.paymybuddy.paymybuddy.dto.TransactionDto;
import com.paymybuddy.paymybuddy.model.*;
import com.paymybuddy.paymybuddy.repository.FriendRepository;
import com.paymybuddy.paymybuddy.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashBoardServiceTest {

    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private FriendRepository friendRepository;
    private TransactionRepository transactionRepository;
    private DashBoardService dashBoardService;

    @Value("${paymybuddy.fee:0.005}")
    private double feeRate;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        passwordEncoder = mock(PasswordEncoder.class);
        friendRepository = mock(FriendRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        dashBoardService = new DashBoardService(userService, passwordEncoder, friendRepository, transactionRepository);

        try {
            java.lang.reflect.Field feeField = DashBoardService.class.getDeclaredField("feeRate");
            feeField.setAccessible(true);
            feeField.set(dashBoardService, 0.005);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set fee rate", e);
        }
    }

    @Test
    void addFriendByEmail_shouldAddFriendSuccessfully() {
        User user = new User();
        user.setIdUser(UUID.randomUUID());
        user.setEmail("user@mail.com");
        User friend = new User();
        friend.setIdUser(UUID.randomUUID());
        friend.setEmail("friend@mail.com");

        when(userService.getUserByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(userService.getUserByEmail("friend@mail.com")).thenReturn(Optional.of(friend));
        when(friendRepository.existsById(any())).thenReturn(false);

        boolean result = dashBoardService.addFriendByEmail("user@mail.com", "friend@mail.com");

        assertTrue(result);
        verify(friendRepository).save(any(Friend.class));
    }

    @Test
    void addFriendByEmail_shouldFail_whenFriendAlreadyExists() {
        User user = new User();
        user.setIdUser(UUID.randomUUID());
        user.setEmail("user@mail.com");
        User friend = new User();
        friend.setIdUser(UUID.randomUUID());
        friend.setEmail("friend@mail.com");

        when(userService.getUserByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(userService.getUserByEmail("friend@mail.com")).thenReturn(Optional.of(friend));
        when(friendRepository.existsById(any())).thenReturn(true);

        boolean result = dashBoardService.addFriendByEmail("user@mail.com", "friend@mail.com");

        assertFalse(result);
        verify(friendRepository, never()).save(any(Friend.class));
    }

    @Test
    void transferMoney_shouldSucceed_whenBalanceIsSufficient() {
        User sender = new User();
        sender.setIdUser(UUID.randomUUID());
        sender.setEmail("sender@mail.com");
        BankAccount senderAccount = new BankAccount();
        senderAccount.setBalance(new BigDecimal("100.00"));
        sender.setBankAccount(senderAccount);

        User receiver = new User();
        receiver.setIdUser(UUID.randomUUID());
        receiver.setEmail("receiver@mail.com");
        BankAccount receiverAccount = new BankAccount();
        receiverAccount.setBalance(new BigDecimal("10.00"));
        receiver.setBankAccount(receiverAccount);

        when(userService.getUserByEmail("sender@mail.com")).thenReturn(Optional.of(sender));
        when(userService.getUserByEmail("receiver@mail.com")).thenReturn(Optional.of(receiver));

        boolean result = dashBoardService.transferMoney("sender@mail.com", "receiver@mail.com", 10.0, "test");

        assertTrue(result);
        assertEquals(new BigDecimal("89.95"), sender.getBankAccount().getBalance());
        assertEquals(new BigDecimal("20.00"), receiver.getBankAccount().getBalance());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void transferMoney_shouldFail_whenBalanceIsInsufficient() {
        User sender = new User();
        sender.setIdUser(UUID.randomUUID());
        sender.setEmail("sender@mail.com");
        BankAccount senderAccount = new BankAccount();
        senderAccount.setBalance(new BigDecimal("5.00"));
        sender.setBankAccount(senderAccount);

        User receiver = new User();
        receiver.setIdUser(UUID.randomUUID());
        receiver.setEmail("receiver@mail.com");
        BankAccount receiverAccount = new BankAccount();
        receiverAccount.setBalance(new BigDecimal("10.00"));
        receiver.setBankAccount(receiverAccount);

        when(userService.getUserByEmail("sender@mail.com")).thenReturn(Optional.of(sender));
        when(userService.getUserByEmail("receiver@mail.com")).thenReturn(Optional.of(receiver));

        boolean result = dashBoardService.transferMoney("sender@mail.com", "receiver@mail.com", 10.0, "test");

        assertFalse(result);
        assertEquals(new BigDecimal("5.00"), sender.getBankAccount().getBalance());
        assertEquals(new BigDecimal("10.00"), receiver.getBankAccount().getBalance());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transferMoney_shouldFail_whenSenderAndReceiverAreSame() {
        boolean result = dashBoardService.transferMoney("same@mail.com", "same@mail.com", 10.0, "test");

        assertFalse(result);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transferMoney_shouldFail_whenAmountIsNegative() {
        boolean result = dashBoardService.transferMoney("sender@mail.com", "receiver@mail.com", -10.0, "test");

        assertFalse(result);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transferMoney_shouldApplyFeeCorrectly() {
        User sender = new User();
        sender.setIdUser(UUID.randomUUID());
        sender.setEmail("sender@mail.com");
        BankAccount senderAccount = new BankAccount();
        senderAccount.setBalance(new BigDecimal("100.00"));
        sender.setBankAccount(senderAccount);

        User receiver = new User();
        receiver.setIdUser(UUID.randomUUID());
        receiver.setEmail("receiver@mail.com");
        BankAccount receiverAccount = new BankAccount();
        receiverAccount.setBalance(new BigDecimal("10.00"));
        receiver.setBankAccount(receiverAccount);

        when(userService.getUserByEmail("sender@mail.com")).thenReturn(Optional.of(sender));
        when(userService.getUserByEmail("receiver@mail.com")).thenReturn(Optional.of(receiver));


        boolean result = dashBoardService.transferMoney("sender@mail.com", "receiver@mail.com", 10.0, "test");


        assertTrue(result);
        assertEquals(new BigDecimal("89.95"), sender.getBankAccount().getBalance()); // 10€ + 0.05€ fee
        assertEquals(new BigDecimal("20.00"), receiver.getBankAccount().getBalance());


        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        Transaction savedTransaction = transactionCaptor.getValue();


        assertEquals(new BigDecimal("0.05"), savedTransaction.getFee());
        assertEquals(new BigDecimal("10.0"), savedTransaction.getAmount());
        assertEquals("test", savedTransaction.getDescription());
    }

    @Test
    void editProfile_shouldReturnProfile_whenUserNotFound() {
        ProfileDto dto = mock(ProfileDto.class);
        BindingResult bindingResult = mock(BindingResult.class);
        UserDetails userDetails = mock(UserDetails.class);
        Model model = mock(Model.class);

        when(userDetails.getUsername()).thenReturn("notfound@mail.com");
        when(userService.getUserByEmail("notfound@mail.com")).thenReturn(Optional.empty());

        String result = dashBoardService.editProfile(dto, bindingResult, userDetails, model);

        assertEquals("profile", result);
        verify(model).addAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void editProfile_shouldReturnProfile_whenPasswordIncorrect() {
        ProfileDto dto = mock(ProfileDto.class);
        BindingResult bindingResult = mock(BindingResult.class);
        UserDetails userDetails = mock(UserDetails.class);
        Model model = mock(Model.class);

        User user = new User();
        user.setEmail("user@mail.com");
        user.setPassword("encoded");
        when(userDetails.getUsername()).thenReturn("user@mail.com");
        when(userService.getUserByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(dto.getPassword()).thenReturn("wrong");
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        String result = dashBoardService.editProfile(dto, bindingResult, userDetails, model);

        assertEquals("redirect:/profile", result);
        verify(bindingResult).rejectValue(eq("password"), anyString(), anyString());
    }

    @Test
    void editProfile_shouldReturnProfile_whenBindingHasErrors() {
        ProfileDto dto = mock(ProfileDto.class);
        BindingResult bindingResult = mock(BindingResult.class);
        UserDetails userDetails = mock(UserDetails.class);
        Model model = mock(Model.class);

        User user = new User();
        user.setEmail("user@mail.com");
        user.setPassword("encoded");
        when(userDetails.getUsername()).thenReturn("user@mail.com");
        when(userService.getUserByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(dto.getPassword()).thenReturn("encoded");
        when(passwordEncoder.matches("encoded", "encoded")).thenReturn(true);
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = dashBoardService.editProfile(dto, bindingResult, userDetails, model);

        assertEquals("profile", result);
        verify(model).addAttribute(eq("user"), any());
    }

    @Test
    void editProfile_shouldRedirect_whenProfileIsValid() {
        ProfileDto dto = mock(ProfileDto.class);
        BindingResult bindingResult = mock(BindingResult.class);
        UserDetails userDetails = mock(UserDetails.class);
        Model model = mock(Model.class);

        User user = new User();
        user.setEmail("user@mail.com");
        user.setPassword("encoded");
        when(userDetails.getUsername()).thenReturn("user@mail.com");
        when(userService.getUserByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(dto.getPassword()).thenReturn("encoded");
        when(passwordEncoder.matches("encoded", "encoded")).thenReturn(true);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(dto.getFirstName()).thenReturn("John");
        when(dto.getLastName()).thenReturn("Doe");
        when(dto.getUserName()).thenReturn("johndoe");
        when(dto.getEmail()).thenReturn("user@mail.com");

        String result = dashBoardService.editProfile(dto, bindingResult, userDetails, model);

        assertEquals("redirect:/profile", result);
        verify(userService).updateUserWithoutPassword(any(User.class));
    }

    @Test
    void getContactsForUser_shouldReturnEmptyList_whenUserNotFound() {
        when(userService.getUserByEmail("notfound@mail.com")).thenReturn(Optional.empty());
        List<ContactDto> contacts = dashBoardService.getContactsForUser("notfound@mail.com");
        assertTrue(contacts.isEmpty());
    }

    @Test
    void getContactsForUser_shouldReturnContacts() {
        User user = new User();
        user.setIdUser(UUID.randomUUID());
        user.setFirstName("User");
        user.setLastName("One");
        user.setEmail("user@mail.com");

        User friend = new User();
        friend.setIdUser(UUID.randomUUID());
        friend.setFirstName("Friend");
        friend.setLastName("Two");
        friend.setEmail("friend@mail.com");

        Friend relation = new Friend(user, friend);

        when(userService.getUserByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(friendRepository.findAll()).thenReturn(List.of(relation));

        List<ContactDto> contacts = dashBoardService.getContactsForUser("user@mail.com");
        assertEquals(1, contacts.size());
        assertEquals("friend@mail.com", contacts.getFirst().getEmail());
        assertEquals("Friend Two", contacts.getFirst().getFullName());
    }

    @Test
    void getTransactionsForUser_shouldReturnEmptyList_whenUserNotFound() {
        when(userService.getUserByEmail("notfound@mail.com")).thenReturn(Optional.empty());
        List<TransactionDto> txs = dashBoardService.getTransactionsForUser("notfound@mail.com");
        assertTrue(txs.isEmpty());
    }

    @Test
    void getTransactionsForUser_shouldReturnTransactions() {
        User user = new User();
        user.setIdUser(UUID.randomUUID());
        user.setFirstName("User");
        user.setLastName("One");
        user.setEmail("user@mail.com");

        User friend = new User();
        friend.setIdUser(UUID.randomUUID());
        friend.setFirstName("Friend");
        friend.setLastName("Two");
        friend.setEmail("friend@mail.com");

        Transaction tx1 = new Transaction();
        tx1.setUserSender(user);
        tx1.setUserReceveir(friend);
        tx1.setAmount(new BigDecimal("10.00"));
        tx1.setDescription("desc1");
        tx1.setDate(LocalDateTime.now());
        tx1.setFee(new BigDecimal("0.05"));

        Transaction tx2 = new Transaction();
        tx2.setUserSender(friend);
        tx2.setUserReceveir(user);
        tx2.setAmount(new BigDecimal("20.00"));
        tx2.setDescription("desc2");
        tx2.setDate(LocalDateTime.now());
        tx2.setFee(new BigDecimal("0.10"));

        when(userService.getUserByEmail(anyString())).thenAnswer(invocation -> {
            String email = invocation.getArgument(0);
            if (email.equals("user@mail.com")) return Optional.of(user);
            if (email.equals("friend@mail.com")) return Optional.of(friend);
            return Optional.empty();
        });
        when(transactionRepository.findAll()).thenReturn(List.of(tx1, tx2));

        List<TransactionDto> txs = dashBoardService.getTransactionsForUser("user@mail.com");
        assertEquals(2, txs.size());
        assertEquals(new BigDecimal("10.00"), txs.get(0).getAmount());
        assertEquals(new BigDecimal("20.00"), txs.get(1).getAmount());
    }

    @Test
    void addFriendByEmail_shouldFail_whenUserOrFriendNotFound() {
        when(userService.getUserByEmail("user@mail.com")).thenReturn(Optional.empty());
        boolean result = dashBoardService.addFriendByEmail("user@mail.com", "friend@mail.com");
        assertFalse(result);

        User user = new User();
        user.setIdUser(UUID.randomUUID());
        when(userService.getUserByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(userService.getUserByEmail("friend@mail.com")).thenReturn(Optional.empty());
        result = dashBoardService.addFriendByEmail("user@mail.com", "friend@mail.com");
        assertFalse(result);
    }

    @Test
    void addFriendByEmail_shouldFail_whenAddingSelf() {
        boolean result = dashBoardService.addFriendByEmail("user@mail.com", "user@mail.com");
        assertFalse(result);
    }

    @Test
    void transferMoney_shouldFail_whenSenderOrReceiverNotFound() {
        when(userService.getUserByEmail("sender@mail.com")).thenReturn(Optional.empty());
        boolean result = dashBoardService.transferMoney("sender@mail.com", "receiver@mail.com", 10.0, "test");
        assertFalse(result);

        User sender = new User();
        sender.setIdUser(UUID.randomUUID());
        when(userService.getUserByEmail("sender@mail.com")).thenReturn(Optional.of(sender));
        when(userService.getUserByEmail("receiver@mail.com")).thenReturn(Optional.empty());
        result = dashBoardService.transferMoney("sender@mail.com", "receiver@mail.com", 10.0, "test");
        assertFalse(result);
    }

    @Test
    void transferMoney_shouldFail_whenAmountIsZero() {
        boolean result = dashBoardService.transferMoney("sender@mail.com", "receiver@mail.com", 0.0, "test");
        assertFalse(result);
    }
}
