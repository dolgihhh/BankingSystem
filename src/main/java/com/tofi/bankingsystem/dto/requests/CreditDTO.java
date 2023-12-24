package com.tofi.bankingsystem.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tofi.bankingsystem.enums.CreditType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditDTO {

    @DecimalMin(value = "100.00", message = "Credit amount must be greater than or equal to 100")
    private BigDecimal amount;

    @JsonProperty("interest_rate")
    @DecimalMin(value = "5.00", message = "Interest rate must be greater than or equal to 5")
    private BigDecimal interestRate;

    @Min(value = 3, message = "Term must be greater than or equal to 3")
    private Integer term;

    @JsonProperty("loan_purpose")
    private String loanPurpose;

    @JsonProperty("credit_type")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Credit type cannot be null")
    private CreditType creditType;

    @JsonProperty("recipient_bank_account_number")
    @NotBlank(message = "Recipient bank account number is required")
    private String recipientBankAccountNumber;


}
