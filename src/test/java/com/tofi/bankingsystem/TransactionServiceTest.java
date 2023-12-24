package com.tofi.bankingsystem;

import com.tofi.bankingsystem.dto.requests.TransactionDTO;
import com.tofi.bankingsystem.exceptions.InsufficientFundsException;
import com.tofi.bankingsystem.exceptions.RecipientAccNotExistsException;
import com.tofi.bankingsystem.exceptions.SameSenderAndRecipientAccException;
import com.tofi.bankingsystem.exceptions.SenderAccNotExistsException;
import com.tofi.bankingsystem.services.BankAccountService;
import com.tofi.bankingsystem.services.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Test
    @Rollback
    @Transactional
    void makeTransaction_Success() throws SenderAccNotExistsException,
            SameSenderAndRecipientAccException, InsufficientFundsException, RecipientAccNotExistsException {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "dolgihpv@mail.ru",
                null,
                Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setSenderBankAccountNumber("BY28BDPB485529012648");
        transactionDTO.setRecipientBankAccountNumber("BY59BDPB359380691174");
        transactionDTO.setAmount(BigDecimal.valueOf(1));
        // Act
        ResponseEntity<?> responseEntity = transactionService.makeTransaction(transactionDTO);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @Rollback
    @Transactional
    void makeTransaction_InsufficientFunds() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "dolgihpv@mail.ru",
                null,
                Collections.emptyList()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setSenderBankAccountNumber("BY59BDPB359380691174");
        transactionDTO.setRecipientBankAccountNumber("BY28BDPB485529012648");
        transactionDTO.setAmount(BigDecimal.valueOf(100000000));

        assertThrows(InsufficientFundsException.class, () -> transactionService.makeTransaction(transactionDTO));
    }

    @Test
    @Rollback
    @Transactional
    void makeTransaction_SenderNotExists()  {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "dolgihpv@mail.ru",
                null,
                Collections.emptyList()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setSenderBankAccountNumber("BY59BDPB359380691274");
        transactionDTO.setRecipientBankAccountNumber("BY28BDPB485529012648");
        transactionDTO.setAmount(BigDecimal.valueOf(1));

        assertThrows(SenderAccNotExistsException.class,
                () -> transactionService.makeTransaction(transactionDTO));
    }

    @Test
    @Rollback
    @Transactional
    void makeTransaction_RecipientNotExists() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "dolgihpv@mail.ru",
                null,
                Collections.emptyList()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setSenderBankAccountNumber("BY28BDPB485529012648");
        transactionDTO.setRecipientBankAccountNumber("BY59BDPB359180691175");
        transactionDTO.setAmount(BigDecimal.valueOf(1));

        assertThrows(RecipientAccNotExistsException.class,
                () -> transactionService.makeTransaction(transactionDTO));
    }

    @Test
    @Rollback
    @Transactional
    void makeTransaction_SameAcc() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "dolgihpv@mail.ru",
                null,
                Collections.emptyList()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setSenderBankAccountNumber("BY59BDPB359380691174");
        transactionDTO.setRecipientBankAccountNumber("BY59BDPB359380691174");
        transactionDTO.setAmount(BigDecimal.valueOf(1));

        assertThrows(SameSenderAndRecipientAccException.class,
                () -> transactionService.makeTransaction(transactionDTO));
    }
}



