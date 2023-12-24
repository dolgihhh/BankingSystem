package com.tofi.bankingsystem.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditPaymentDTO {

    @JsonProperty("credit_id")
    @Min(value = 1, message = "Id must be greater than or equal to 1")
    private Long creditId;

    @JsonProperty("bank_account_number")
    @NotBlank(message = "Bank account number is required")
    private String bankAccountNumber;
}
