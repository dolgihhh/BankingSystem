package com.tofi.bankingsystem.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingDTO {

    @JsonProperty("rounding_value")
    @DecimalMin(value = "0.02", message = "Rounding value must be greater than or equal to 0.02")
    private BigDecimal roundingValue;
}
