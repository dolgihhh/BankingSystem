package com.tofi.bankingsystem.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFirstLoginDTO {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
