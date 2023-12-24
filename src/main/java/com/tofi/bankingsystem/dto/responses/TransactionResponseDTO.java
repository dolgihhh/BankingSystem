package com.tofi.bankingsystem.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDTO {

    private UUID id;

    @JsonProperty("sender_bank_account_number")
    private String senderBankAccountNumber;

    @JsonProperty("recipient_bank_account_number")
    private String recipientBankAccountNumber;

    private BigDecimal amount;

    private String description;

    @JsonProperty("transaction_date")
    private LocalDateTime transactionDate;
}
