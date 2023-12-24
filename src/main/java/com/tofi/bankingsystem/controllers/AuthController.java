package com.tofi.bankingsystem.controllers;

import com.tofi.bankingsystem.dto.requests.UserFirstLoginDTO;
import com.tofi.bankingsystem.dto.requests.UserRegistrationDTO;
import com.tofi.bankingsystem.dto.requests.UserSecondLoginDTO;
import com.tofi.bankingsystem.exceptions.ExpiredVerificationCodeException;
import com.tofi.bankingsystem.exceptions.IncorrectVerificationCodeException;
import com.tofi.bankingsystem.exceptions.UserAlreadyExistsException;
import com.tofi.bankingsystem.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/first-login")
    public ResponseEntity<?> firstAuthenticateUser(@Valid @RequestBody UserFirstLoginDTO loginDTO) {

        return authService.firstAuthenticateUser(loginDTO);
    }

    //2fa
    @PostMapping("/second-login")
    public ResponseEntity<?> secondAuthenticateUser(@Valid @RequestBody UserSecondLoginDTO loginDTO)
            throws IncorrectVerificationCodeException, ExpiredVerificationCodeException {

        return authService.secondAuthenticateUser(loginDTO);
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) throws UserAlreadyExistsException {

        return authService.registerUser(registrationDTO);
    }
}

