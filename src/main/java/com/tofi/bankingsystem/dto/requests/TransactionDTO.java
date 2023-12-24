package com.tofi.bankingsystem.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tofi.bankingsystem.entities.BankAccount;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    @JsonProperty("sender_bank_account_number")
    @NotBlank(message = "Sender bank account number is required")
    private String senderBankAccountNumber;

    @JsonProperty("recipient_bank_account_number")
    @NotBlank(message = "Recipient bank account number is required")
    private String recipientBankAccountNumber;

    @DecimalMin(value = "0.01", message = "Amount must be greater than or equal to 0.01")
    private BigDecimal amount;

    private String description;
}
