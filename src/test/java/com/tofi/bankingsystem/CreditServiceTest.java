package com.tofi.bankingsystem;

import com.tofi.bankingsystem.dto.requests.CreditDTO;
import com.tofi.bankingsystem.dto.requests.CreditPaymentDTO;
import com.tofi.bankingsystem.dto.responses.CreditResponseDTO;
import com.tofi.bankingsystem.enums.CreditType;
import com.tofi.bankingsystem.exceptions.CreditNotExistsException;
import com.tofi.bankingsystem.exceptions.InsufficientFundsException;
import com.tofi.bankingsystem.exceptions.RecipientAccNotExistsException;
import com.tofi.bankingsystem.exceptions.SenderAccNotExistsException;
import com.tofi.bankingsystem.services.CreditService;
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
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CreditServiceTest {

    @Autowired
    private CreditService creditService;

    @Test
    @Transactional
    @Rollback
    void takeCredit_Success() throws RecipientAccNotExistsException {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "dolgihpv@mail.ru",
                null,
                Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CreditDTO creditDTO = new CreditDTO();
        creditDTO.setRecipientBankAccountNumber("BY28BDPB485529012648");
        creditDTO.setAmount(BigDecimal.valueOf(1000));
        creditDTO.setInterestRate(BigDecimal.valueOf(10));
        creditDTO.setTerm(12);
        creditDTO.setCreditType(CreditType.ANNUITY);
        creditDTO.setLoanPurpose("Loan for a car");

        ResponseEntity<?> responseEntity = creditService.takeCredit(creditDTO);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @Transactional
    @Rollback
    void makeCreditPayment_Success() throws SenderAccNotExistsException, CreditNotExistsException,
            InsufficientFundsException, RecipientAccNotExistsException {

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "dolgihpv@mail.ru",
                null,
                Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CreditDTO creditDTO = new CreditDTO();
        creditDTO.setRecipientBankAccountNumber("BY59BDPB359380691174");
        creditDTO.setAmount(BigDecimal.valueOf(1000));
        creditDTO.setInterestRate(BigDecimal.valueOf(10));
        creditDTO.setTerm(12);
        creditDTO.setCreditType(CreditType.ANNUITY);
        creditDTO.setLoanPurpose("Loan for a car");

        ResponseEntity<?> responseEntity = creditService.takeCredit(creditDTO);
        HashMap<String, Object> responseBody = (HashMap<String, Object>) responseEntity.getBody();
        CreditResponseDTO creditResponseDTO = (CreditResponseDTO) responseBody.get("credit_info");

        Long creditId = creditResponseDTO.getId();

        CreditPaymentDTO creditPaymentDTO = new CreditPaymentDTO();
        creditPaymentDTO.setCreditId(creditId);
        creditPaymentDTO.setBankAccountNumber("BY28BDPB485529012648");

        ResponseEntity<?> responseEntityPayment = creditService.makeCreditPayment(creditPaymentDTO);

        assertNotNull(responseEntityPayment);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
