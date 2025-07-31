package com.paymybuddy.paymybuddy.service;

import com.paymybuddy.paymybuddy.dto.ContactDto;
import com.paymybuddy.paymybuddy.dto.ProfileDto;
import com.paymybuddy.paymybuddy.dto.TransactionDto;
import com.paymybuddy.paymybuddy.model.User;
import com.paymybuddy.paymybuddy.model.Friend;
import com.paymybuddy.paymybuddy.model.Transaction;
import com.paymybuddy.paymybuddy.repository.FriendRepository;
import com.paymybuddy.paymybuddy.repository.TransactionRepository;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DashBoardService {

    private static final Logger logger = LogManager.getLogger(DashBoardService.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final FriendRepository friendRepository;
    private final TransactionRepository transactionRepository;

    @Value("${paymybuddy.fee:0.005}")
    private double feeRate;

    public DashBoardService(UserService userService, PasswordEncoder passwordEncoder, FriendRepository friendRepository, TransactionRepository transactionRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.friendRepository = friendRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Get a user by their email.
     * @param email the user's email
     * @return Optional containing the user if found
     */
    public Optional<User> getUserByEmail(String email) {
        logger.debug("Fetching user by email: {}", email);
        return userService.getUserByEmail(email);
    }

    /**
     * Create a ProfileDto from a User.
     * @param user the user
     * @return the ProfileDto
     */
    public ProfileDto createProfileDto(User user) {
        logger.debug("Creating ProfileDto for user: {}", user.getEmail());
        return ProfileDto.fromUser(user);
    }

    /**
     * Edit the profile of a user.
     * @param profileDto the profile data
     * @param bindingResult validation result
     * @param userDetails authenticated user details
     * @param model Spring MVC model
     * @return view name
     */
    public String editProfile(@Valid ProfileDto profileDto,
                              BindingResult bindingResult,
                              UserDetails userDetails,
                              Model model) {
        logger.info("Profile update process started for user '{}'.", userDetails.getUsername());
        Optional<User> userOptional = userService.getUserByEmail(userDetails.getUsername());
        if (userOptional.isEmpty()) {
            logger.error("User not found for email '{}'.", userDetails.getUsername());
            model.addAttribute("errorMessage", "User not found");
            return "profile";
        }

        User user = userOptional.get();

        // Password verification
        if (!passwordEncoder.matches(profileDto.getPassword(), user.getPassword())) {
            logger.warn("Incorrect password provided for user '{}'.", user.getEmail());
            bindingResult.rejectValue("password", "error.password", "Mot de passe incorrect");
        }

        if (bindingResult.hasErrors()) {
            logger.info("Validation errors occurred during profile update for user '{}'.", user.getEmail());
            model.addAttribute("user", user);
            return "profile";
        }

        logger.debug("Updating profile for user '{}'.", user.getEmail());
        user.setFirstName(profileDto.getFirstName());
        user.setLastName(profileDto.getLastName());
        user.setUserName(profileDto.getUserName());
        user.setEmail(profileDto.getEmail());

        userService.updateUserWithoutPassword(user);
        logger.info("Profile successfully updated for user '{}'.", user.getEmail());
        return "redirect:/profile";
    }

    /**
     * Add a friend by email for a user.
     * @param userEmail the user's email
     * @param friendEmail the friend's email
     * @return true if the friend was added, false otherwise
     */
    public boolean addFriendByEmail(String userEmail, String friendEmail) {
        logger.debug("Attempting to add friend by email: '{}' for user '{}'", friendEmail, userEmail);
        if (userEmail.equalsIgnoreCase(friendEmail)) {
            logger.warn("User tried to add themselves as a friend.");
            return false;
        }
        Optional<User> userOptional = userService.getUserByEmail(userEmail);
        Optional<User> friendOptional = userService.getUserByEmail(friendEmail);
        if (userOptional.isEmpty() || friendOptional.isEmpty()) {
            logger.warn("One or both users not found: '{}' or '{}'", userEmail, friendEmail);
            return false;
        }
        User user = userOptional.get();
        User friend = friendOptional.get();

        // Check if the friendship already exists in either direction
        boolean alreadyExists = friendRepository.existsById(
            new com.paymybuddy.paymybuddy.model.FriendId(user.getIdUser(), friend.getIdUser())
        ) || friendRepository.existsById(
            new com.paymybuddy.paymybuddy.model.FriendId(friend.getIdUser(), user.getIdUser())
        );
        if (alreadyExists) {
            logger.info("Friendship already exists between '{}' and '{}'", userEmail, friendEmail);
            return false;
        }
        Friend relation = new Friend(user, friend);
        friendRepository.save(relation);
        logger.info("Friendship created between '{}' and '{}'", userEmail, friendEmail);
        return true;
    }

    /**
     * Transfer money from one user to another, applying a fee.
     * @param senderEmail the sender's email
     * @param receiverEmail the receiver's email
     * @param amount the amount to transfer
     * @param description the transaction description
     * @return true if the transfer was successful, false otherwise
     */
    @Transactional
    public boolean transferMoney(String senderEmail, String receiverEmail, double amount, String description) {
        logger.debug("Transfer request: {}€ from '{}' to '{}' with description '{}'", amount, senderEmail, receiverEmail, description);

        if (amount <= 0) {
            logger.warn("Invalid transfer amount: {}", amount);
            return false;
        }
        if (senderEmail.equalsIgnoreCase(receiverEmail)) {
            logger.warn("User tried to transfer money to themselves.");
            return false;
        }

        Optional<User> senderOptional = userService.getUserByEmail(senderEmail);
        Optional<User> receiverOptional = userService.getUserByEmail(receiverEmail);

        if (senderOptional.isEmpty() || receiverOptional.isEmpty()) {
            logger.warn("Sender or receiver not found.");
            return false;
        }

        User sender = senderOptional.get();
        User receiver = receiverOptional.get();

        BigDecimal transferAmount = BigDecimal.valueOf(amount);
        BigDecimal fee = transferAmount.multiply(BigDecimal.valueOf(feeRate)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalDebit = transferAmount.add(fee);

        // Check if sender has enough balance
        if (sender.getBankAccount().getBalance().compareTo(totalDebit) < 0) {
            logger.warn("Insufficient balance for user '{}'.", sender.getEmail());
            return false;
        }

        // Update balances
        sender.getBankAccount().setBalance(sender.getBankAccount().getBalance().subtract(totalDebit));
        receiver.getBankAccount().setBalance(receiver.getBankAccount().getBalance().add(transferAmount));

        userService.saveUser(sender);
        userService.saveUser(receiver);

        // Save the transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(transferAmount);
        transaction.setDate(LocalDateTime.now());
        transaction.setDescription(description);
        transaction.setUserSender(sender);
        transaction.setUserReceveir(receiver);
        transaction.setFee(fee);

        transactionRepository.save(transaction);

        logger.info("Transfer of {}€ from '{}' to '{}' completed with a fee of {}€.", amount, senderEmail, receiverEmail, fee);
        return true;
    }

    /**
     * Get the list of contacts (friends) for a user.
     * @param userEmail the user's email
     * @return list of ContactDto
     */
    public List<ContactDto> getContactsForUser(String userEmail) {
        Optional<User> userOpt = userService.getUserByEmail(userEmail);
        if (userOpt.isEmpty()) {
            logger.warn("No user found for email '{}'", userEmail);
            return Collections.emptyList();
        }
        User user = userOpt.get();
        List<Friend> friends = friendRepository.findAll().stream()
                .filter(f -> f.getUser1().getIdUser().equals(user.getIdUser()) || f.getUser2().getIdUser().equals(user.getIdUser()))
                .toList();

        List<User> contacts = new ArrayList<>();
        for (Friend f : friends) {
            if (f.getUser1().getIdUser().equals(user.getIdUser())) {
                contacts.add(f.getUser2());
            } else {
                contacts.add(f.getUser1());
            }
        }
        return contacts.stream()
                .map(u -> new ContactDto(u.getEmail(), u.getFirstName() + " " + u.getLastName()))
                .toList();
    }

    /**
     * Get the list of transactions for a user.
     * @param userEmail the user's email
     * @return list of TransactionDto
     */
    public List<TransactionDto> getTransactionsForUser(String userEmail) {
        Optional<User> userOpt = userService.getUserByEmail(userEmail);
        if (userOpt.isEmpty()) {
            logger.warn("No user found for email '{}'", userEmail);
            return Collections.emptyList();
        }
        User user = userOpt.get();
        List<Transaction> all = transactionRepository.findAll();
        return all.stream()
                .filter(t -> t.getUserSender().getIdUser().equals(user.getIdUser()) || t.getUserReceveir().getIdUser().equals(user.getIdUser()))
                .map(t -> {
                    String contactName;
                    if (t.getUserSender().getIdUser().equals(user.getIdUser())) {
                        contactName = t.getUserReceveir().getFirstName() + " " + t.getUserReceveir().getLastName();
                    } else {
                        contactName = t.getUserSender().getFirstName() + " " + t.getUserSender().getLastName();
                    }
                    return new TransactionDto(contactName, t.getDescription(), t.getAmount());
                })
                .toList();
    }
}
