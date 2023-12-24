package com.tofi.bankingsystem.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tofi.bankingsystem.enums.CreditType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditResponseDTO {

    private Long id;

    private BigDecimal amount;

    @JsonProperty("interest_rate")
    private BigDecimal interestRate;

    private Integer term;

    @JsonProperty("balance_of_debt")
    private BigDecimal balanceOfDebt;

    @JsonProperty("balance_of_main_debt")
    private BigDecimal balanceOfMainDebt;

    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @JsonProperty("is_repaid")
    private boolean isRepaid;

    @JsonProperty("monthly_payment")
    private BigDecimal monthlyPayment;

    @JsonProperty("monthly_main_debt_payment")
    private BigDecimal monthlyMainDebtPayment;

    @JsonProperty("loan_purpose")
    private String loanPurpose;

    @Enumerated(EnumType.STRING)
    @JsonProperty("credit_type")
    private CreditType creditType;
}
