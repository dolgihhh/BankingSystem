package com.tofi.bankingsystem.entities;

import com.tofi.bankingsystem.enums.CreditType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name="Credits")
@Table(name="credits")
@Data
@AllArgsConstructor
public class Credit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "bank_account_number", referencedColumnName = "account_number", nullable = false)
    private BankAccount bankAccount;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate;

    @Column(nullable = false)
    private Integer term;

    @Column(name = "balance_of_debt", nullable = false)
    private BigDecimal balanceOfDebt;

    @Column(name = "balance_of_main_debt")
    private BigDecimal balanceOfMainDebt;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "is_repaid", nullable = false)
    private boolean isRepaid;

    @Column(name = "loan_purpose")
    private String loanPurpose;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "monthly_payment", nullable = false)
    private BigDecimal monthlyPayment;

    @Column(name = "monthly_main_debt_payment")
    private BigDecimal monthlyMainDebtPayment;

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_type", nullable = false)
    private CreditType creditType;


    public Credit() {
        this.startDate = LocalDateTime.now();
        this.isRepaid = false;
    }
}
