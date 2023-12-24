package com.tofi.bankingsystem.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Entity(name="Savings")
@Table(name="savings")
@Data
@AllArgsConstructor
public class Saving {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "bank_account_number", referencedColumnName = "account_number", nullable = false)
    private BankAccount bankAccount;

    @Column(name = "total_accumulated", nullable = false)
    private BigDecimal totalAccumulated;

    @Column(name = "rounding_value", nullable = false)
    private BigDecimal roundingValue;

    @Column(name = "is_on", nullable = false)
    private boolean isOn;

    @Column(name = "is_frozen", nullable = false)
    private boolean isFrozen;


    public Saving() {
        this.totalAccumulated = BigDecimal.ZERO;
        this.isOn = true;
        this.isFrozen = false;
    }
}
