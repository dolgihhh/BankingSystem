package com.tofi.bankingsystem.services;

import com.tofi.bankingsystem.dto.requests.TransactionDTO;
import com.tofi.bankingsystem.dto.responses.TransactionResponseDTO;
import com.tofi.bankingsystem.entities.BankAccount;
import com.tofi.bankingsystem.entities.Saving;
import com.tofi.bankingsystem.entities.Transaction;
import com.tofi.bankingsystem.entities.User;
import com.tofi.bankingsystem.exceptions.InsufficientFundsException;
import com.tofi.bankingsystem.exceptions.RecipientAccNotExistsException;
import com.tofi.bankingsystem.exceptions.SameSenderAndRecipientAccException;
import com.tofi.bankingsystem.exceptions.SenderAccNotExistsException;
import com.tofi.bankingsystem.repositiries.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final UserService userService;
    private final SavingService savingService;
    private final BankAccountService bankAccountService;
    private final TransactionRepository transactionRepository;

    @Transactional
    public ResponseEntity<?> makeTransaction(TransactionDTO transactionDTO)
            throws SameSenderAndRecipientAccException,
                   SenderAccNotExistsException, RecipientAccNotExistsException,
                   InsufficientFundsException {

        if (transactionDTO.getSenderBankAccountNumber()
                          .equals(transactionDTO.getRecipientBankAccountNumber())) {
            throw new SameSenderAndRecipientAccException("Same sender and recipient bank account");
        }

        String userEmail = SecurityContextHolder.getContext()
                                                .getAuthentication()
                                                .getName();
        User user = userService.findByEmail(userEmail)
                               .orElseThrow(() -> new UsernameNotFoundException(""));
        Saving saving = user.getSaving();

        BankAccount senderBankAccount =
                bankAccountService.findBankAccountByNumber(
                                          transactionDTO.getSenderBankAccountNumber())
                                  .orElseThrow(() -> new SenderAccNotExistsException(
                                          "You don't have such а" +
                                          "bank account"));

        if (!user.equals(senderBankAccount.getUser()) || senderBankAccount.getCredit() != null) {
            throw new SenderAccNotExistsException("You don't have such а bank account");
        }

        if (saving != null && saving.getBankAccount()
                                    .equals(senderBankAccount) && saving.isFrozen()) {
            throw new SenderAccNotExistsException("Savings is frozen");
        }

        BankAccount recipientBankAccount =
                bankAccountService.findBankAccountByNumber(
                                          transactionDTO.getRecipientBankAccountNumber())
                                  .orElseThrow(() -> new RecipientAccNotExistsException("No such " +
                                                                                        "recipient bank account"));

        if (recipientBankAccount.getSaving() != null || recipientBankAccount.getCredit() != null) {
            throw new RecipientAccNotExistsException("No such recipient bank account");
        }

        if (senderBankAccount.getSaving() != null && !user.equals(recipientBankAccount.getUser())) {
            throw new RecipientAccNotExistsException("Can't transfer savings to another user");
        }

        if (senderBankAccount.getBalance()
                             .compareTo(transactionDTO.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds in the bank account");
        }

        Transaction newTransaction = new Transaction();
        newTransaction.setSenderBankAccount(senderBankAccount);
        newTransaction.setRecipientBankAccount(recipientBankAccount);
        newTransaction.setAmount(transactionDTO.getAmount());
        newTransaction.setDescription(transactionDTO.getDescription());
        transactionRepository.save(newTransaction);
        System.out.println(newTransaction.getTransactionDate());

        bankAccountService.reduceBalance(transactionDTO.getAmount(), senderBankAccount);
        bankAccountService.increaseBalance(transactionDTO.getAmount(), recipientBankAccount);

        TransactionResponseDTO transactionResponseDTO = getTransactionResponseDTO(newTransaction);

        createSavingTransaction(saving, senderBankAccount, transactionDTO.getAmount(),
                                newTransaction.getId());

        return ResponseEntity.ok(transactionResponseDTO);
    }

    private void createSavingTransaction(Saving saving, BankAccount senderBankAccount,
                                         BigDecimal amount,
                                         UUID mainTransactionId) {
        if (saving != null && saving.isOn() && !saving.getBankAccount()
                                                      .equals(senderBankAccount)) {
            BigDecimal roundingValue = saving.getRoundingValue();
            BigDecimal savingAmount;
            BigDecimal ratio = amount.divide(roundingValue, MathContext.DECIMAL128);
            BigDecimal integerPart = new BigDecimal(ratio.intValue());
            BigDecimal fractionalPart = ratio.remainder(BigDecimal.ONE);
            if (fractionalPart.compareTo(BigDecimal.ZERO) != 0) {
                savingAmount = integerPart.add(BigDecimal.ONE)
                                          .multiply(roundingValue)
                                          .subtract(amount);
                if (senderBankAccount.getBalance()
                                     .compareTo(savingAmount) >= 0) {
                    Transaction savingTransaction = new Transaction();
                    savingTransaction.setAmount(savingAmount);
                    savingTransaction.setSenderBankAccount(senderBankAccount);
                    savingTransaction.setRecipientBankAccount(saving.getBankAccount());
                    savingTransaction.setDescription("Round-up saving!");
                    bankAccountService.reduceBalance(savingAmount, senderBankAccount);
                    bankAccountService.increaseBalance(savingAmount, saving.getBankAccount());
                    savingService.increaseTotalAccumulated(savingAmount, saving);
                    transactionRepository.save(savingTransaction);
                    System.out.println(savingTransaction.getTransactionDate());
                }
            }
        }
    }

    private static TransactionResponseDTO getTransactionResponseDTO(Transaction newTransaction) {
        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();
        transactionResponseDTO.setId(newTransaction.getId());
        transactionResponseDTO.setSenderBankAccountNumber(newTransaction.getSenderBankAccount()
                                                                        .getNumber());
        transactionResponseDTO.setRecipientBankAccountNumber(
                newTransaction.getRecipientBankAccount()
                              .getNumber());
        transactionResponseDTO.setAmount(newTransaction.getAmount());
        transactionResponseDTO.setTransactionDate(newTransaction.getTransactionDate());
        transactionResponseDTO.setDescription(newTransaction.getDescription());

        return transactionResponseDTO;
    }

    public void takeCreditTransaction(BankAccount creditBankAccount,
                                      BankAccount recipientBankAccount,
                                      BigDecimal creditAmount, BigDecimal debtAmount) {
        Transaction takeCreditTransaction = new Transaction();
        takeCreditTransaction.setAmount(creditAmount);
        takeCreditTransaction.setSenderBankAccount(creditBankAccount);
        takeCreditTransaction.setRecipientBankAccount(recipientBankAccount);
        takeCreditTransaction.setDescription("Credit money transfer");
        bankAccountService.reduceBalance(debtAmount, creditBankAccount);
        bankAccountService.increaseBalance(creditAmount, recipientBankAccount);
        transactionRepository.save(takeCreditTransaction);
    }

    public void creditPaymentTransaction(BankAccount creditBankAccount,
                                         BankAccount senderBankAccount, BigDecimal monthlyPayment) {
        Transaction takeCreditTransaction = new Transaction();
        takeCreditTransaction.setAmount(monthlyPayment);
        takeCreditTransaction.setSenderBankAccount(senderBankAccount);
        takeCreditTransaction.setRecipientBankAccount(creditBankAccount);
        takeCreditTransaction.setDescription("Credit payment");
        bankAccountService.reduceBalance(monthlyPayment, senderBankAccount);
        bankAccountService.increaseBalance(monthlyPayment, creditBankAccount);
        transactionRepository.save(takeCreditTransaction);
    }

    public ResponseEntity<?> getReceivedTransaction() {
        String userEmail = SecurityContextHolder.getContext()
                                                .getAuthentication()
                                                .getName();
        User user = userService.findByEmail(userEmail)
                               .orElseThrow(() -> new UsernameNotFoundException(""));
        List<BankAccount> bankAccounts = user.getBankAccounts();
        List<TransactionResponseDTO> receivedTransactions = new ArrayList<>();
        if (bankAccounts != null) {
            for (BankAccount bankAccount : bankAccounts) {
                if (bankAccount.getReceivedTransactions() != null && bankAccount.getCredit() == null
                    && bankAccount.getSaving() == null) {
                    for (Transaction transaction : bankAccount.getReceivedTransactions()) {
                        receivedTransactions.add(getTransactionResponseDTO(transaction));
                    }
                }
            }
        }
        receivedTransactions.sort(Comparator.comparing(TransactionResponseDTO::getTransactionDate)
                                            .reversed());

        return ResponseEntity.status(HttpStatus.OK)
                             .body(receivedTransactions);
    }

    public ResponseEntity<?> getSentTransaction() {
        String userEmail = SecurityContextHolder.getContext()
                                                .getAuthentication()
                                                .getName();
        User user = userService.findByEmail(userEmail)
                               .orElseThrow(() -> new UsernameNotFoundException(""));

        List<BankAccount> bankAccounts = user.getBankAccounts();
        List<TransactionResponseDTO> sentTransactions = new ArrayList<>();
        if (bankAccounts != null) {
            for (BankAccount bankAccount : bankAccounts) {
                if (bankAccount.getSentTransactions() != null && bankAccount.getCredit() == null
                    && bankAccount.getSaving() == null) {
                    for (Transaction transaction : bankAccount.getSentTransactions()) {
                        sentTransactions.add(getTransactionResponseDTO(transaction));
                    }
                }
            }
        }
        sentTransactions.sort(Comparator.comparing(TransactionResponseDTO::getTransactionDate)
                                        .reversed());

        return ResponseEntity.status(HttpStatus.OK)
                             .body(sentTransactions);
    }
}
