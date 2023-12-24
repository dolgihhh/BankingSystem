package com.tofi.bankingsystem.controllers;

import com.tofi.bankingsystem.dto.requests.SavingDTO;
import com.tofi.bankingsystem.services.SavingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/savings")
@Validated
public class SavingController {

    private final SavingService savingService;

    @PostMapping("/enable")
    public ResponseEntity<?> enableSavings(@Valid @RequestBody SavingDTO savingDTO) {

        return savingService.enableSavings(savingDTO);
    }

    @PostMapping("/disable")
    public ResponseEntity<?> disableSavings() {

        return savingService.disableSavings();
    }

    @GetMapping("")
    public ResponseEntity<?> getSavings() {

        return savingService.getSavings();
    }

    //frozen, unfrozen
}
