package com.tofi.bankingsystem.controllers;

import com.tofi.bankingsystem.services.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bank-accounts")
@Validated
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @PostMapping("")
    public ResponseEntity<?> createBankAccount() {
        System.out.println("popalo v controller");
        return bankAccountService.createBankAccount();
    }

    @GetMapping("")
    public ResponseEntity<?> getBankAccounts() {

        return bankAccountService.getBankAccounts();
    }
}
