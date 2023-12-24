package com.tofi.bankingsystem;

import com.tofi.bankingsystem.controllers.BankAccountController;
import com.tofi.bankingsystem.dto.responses.BankAccountDTO;
import com.tofi.bankingsystem.entities.BankAccount;
import com.tofi.bankingsystem.services.BankAccountService;
import com.tofi.bankingsystem.utils.BankAccountNumberUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class BankAccountServiceTest {

    @Autowired
    private BankAccountService bankAccountService;

    @Test
    @Rollback
    @Transactional
    void createBankAccount_Success() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "dolgihpv@mail.ru",
                null,
                Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println((SecurityContextHolder.getContext().getAuthentication().getName()));

        ResponseEntity<?> responseEntity = bankAccountService.createBankAccount();

        assertNotNull(responseEntity);  // Ensure responseEntity is not null
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @Rollback
    @Transactional
    void createBankAccount_Failure() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user_with_error@mail.com", // введите существующий email пользователя с ошибкой
                null,
                Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println((SecurityContextHolder.getContext().getAuthentication().getName()));

        ResponseEntity<?> responseEntity = bankAccountService.createBankAccount();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

        Map<String, Object> responseBody = (Map<String, Object>) responseEntity.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
        assertNotNull(responseBody.get("error"));
        System.out.println("Error message: " + responseBody.get("error"));
    }

}
