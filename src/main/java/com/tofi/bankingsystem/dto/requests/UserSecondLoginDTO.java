package com.tofi.bankingsystem.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSecondLoginDTO {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Verification code is required")
    @JsonProperty("verification_code")
    private String verificationCode;
}
