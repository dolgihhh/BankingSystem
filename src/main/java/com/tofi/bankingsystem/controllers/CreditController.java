package com.tofi.bankingsystem.controllers;

import com.tofi.bankingsystem.dto.requests.CreditDTO;
import com.tofi.bankingsystem.dto.requests.CreditPaymentDTO;
import com.tofi.bankingsystem.exceptions.CreditNotExistsException;
import com.tofi.bankingsystem.exceptions.InsufficientFundsException;
import com.tofi.bankingsystem.exceptions.RecipientAccNotExistsException;
import com.tofi.bankingsystem.exceptions.SenderAccNotExistsException;
import com.tofi.bankingsystem.services.CreditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/credits")
@Validated
public class CreditController {

    private final CreditService creditService;

    @PostMapping("")
    public ResponseEntity<?> takeCredit(@Valid @RequestBody CreditDTO creditDTO) throws RecipientAccNotExistsException {

        return creditService.takeCredit(creditDTO);
    }

    @PostMapping("/payment")
    public ResponseEntity<?> makeCreditPayment(@Valid @RequestBody CreditPaymentDTO creditPaymentDTO)
            throws SenderAccNotExistsException, CreditNotExistsException, InsufficientFundsException {

        return creditService.makeCreditPayment(creditPaymentDTO);
    }

    @GetMapping("")
    public ResponseEntity<?> getCredits() {

        return creditService.getCredits();
    }
}
