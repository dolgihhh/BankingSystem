package com.tofi.bankingsystem.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingResponseDTO {

    private String message;

    @JsonProperty("total_accumulated")
    private BigDecimal totalAccumulated;

    @JsonProperty("bank_account_number")
    private String bankAccountNumber;

    private BigDecimal balance;

    @JsonProperty("rounding_value")
    private BigDecimal roundingValue;

    @JsonProperty("is_on")
    private boolean isOn;
}
