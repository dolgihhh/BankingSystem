package com.tofi.bankingsystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name="BankAccounts")
@Table(name="bank_accounts")
@Data
@AllArgsConstructor
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, name = "account_number")
    private String number;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(name = "opening_date", nullable = false)
    private LocalDateTime openingDate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "senderBankAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> sentTransactions;

    @JsonIgnore
    @OneToMany(mappedBy = "recipientBankAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> receivedTransactions;

    @JsonIgnore
    @OneToOne(mappedBy = "bankAccount", fetch = FetchType.LAZY)
    private Saving saving;

    @JsonIgnore
    @OneToOne(mappedBy = "bankAccount", fetch = FetchType.LAZY)
    private Credit credit;


    public BankAccount() {
        this.balance = BigDecimal.ZERO;
        this.openingDate = LocalDateTime.now();
    }
}
