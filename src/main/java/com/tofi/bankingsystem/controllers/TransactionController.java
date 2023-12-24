package com.tofi.bankingsystem.controllers;

import com.tofi.bankingsystem.dto.requests.TransactionDTO;
import com.tofi.bankingsystem.exceptions.InsufficientFundsException;
import com.tofi.bankingsystem.exceptions.RecipientAccNotExistsException;
import com.tofi.bankingsystem.exceptions.SameSenderAndRecipientAccException;
import com.tofi.bankingsystem.exceptions.SenderAccNotExistsException;
import com.tofi.bankingsystem.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("")
    public ResponseEntity<?> makeTransaction(@Valid @RequestBody TransactionDTO transactionDTO) throws SameSenderAndRecipientAccException,
            RecipientAccNotExistsException, SenderAccNotExistsException, InsufficientFundsException {

        return transactionService.makeTransaction(transactionDTO);
    }

    @GetMapping("/received")
    public ResponseEntity<?> getReceivedTransaction() {

        return transactionService.getReceivedTransaction();
    }

    @GetMapping("/sent")
    public ResponseEntity<?> getSentTransaction() {

        return transactionService.getSentTransaction();
    }

}
