package com.tofi.bankingsystem.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name="Transactions")
@Table(name="transactions")
@Data
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_bank_account_number", referencedColumnName = "account_number", nullable = false)
    private BankAccount senderBankAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_bank_account_number", referencedColumnName = "account_number", nullable = false)
    private BankAccount recipientBankAccount;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column
    private String description;


    public Transaction() {
        this.transactionDate = LocalDateTime.now();
    }
}
