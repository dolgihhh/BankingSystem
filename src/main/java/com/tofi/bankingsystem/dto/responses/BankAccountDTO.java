package com.tofi.bankingsystem.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountDTO {

    private String number;
    private BigDecimal balance;

    @JsonProperty("opening_date")
    private LocalDateTime openingDate;
}
