package com.tofi.bankingsystem.services;

import com.tofi.bankingsystem.dto.requests.UserRegistrationDTO;
import com.tofi.bankingsystem.dto.responses.BankAccountDTO;
import com.tofi.bankingsystem.dto.responses.LoginResponseDTO;
import com.tofi.bankingsystem.entities.BankAccount;
import com.tofi.bankingsystem.entities.User;
import com.tofi.bankingsystem.repositiries.BankAccountRepository;
import com.tofi.bankingsystem.utils.BankAccountNumberUtils;
import com.tofi.bankingsystem.utils.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final BankAccountNumberUtils bankAccountNumberUtils;
    private final UserService userService;

    @Transactional
    public ResponseEntity<?> createBankAccount() {
        try {
            BankAccount newBankAccount = createBankAccountInDB();

            BankAccountDTO bankAccountDTO = new BankAccountDTO();
            bankAccountDTO.setNumber(newBankAccount.getNumber());
            bankAccountDTO.setBalance(newBankAccount.getBalance());
            bankAccountDTO.setOpeningDate(newBankAccount.getOpeningDate());

            return ResponseEntity.status(HttpStatus.OK).body(bankAccountDTO);
        } catch (Exception e) {

            return ResponseHandler.generateErrorResponse(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public BankAccount createBankAccountInDB() {
        String generatedAccountNumber = bankAccountNumberUtils.generateBankAccountNumber();

        while (bankAccountNumberAlreadyExists(generatedAccountNumber)) {
            generatedAccountNumber = bankAccountNumberUtils.generateBankAccountNumber();
        }

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(userEmail);
        User user = userService.findByEmail(userEmail).orElse(null);

        BankAccount newBankAccount = new BankAccount();
        newBankAccount.setNumber(generatedAccountNumber);
        newBankAccount.setUser(user);
        //newBankAccount.setBalance(new BigDecimal("100.07")); // for default money value

        bankAccountRepository.save(newBankAccount);
        return newBankAccount;
    }

    public boolean bankAccountNumberAlreadyExists(String number) {

        return bankAccountRepository.existsByNumber(number);
    }

    public Optional<BankAccount> findBankAccountByNumber(String number) {

        return bankAccountRepository.findByNumber(number);
    }

    public void reduceBalance(BigDecimal amount, BankAccount bankAccount) {
        BigDecimal newBalance = bankAccount.getBalance().subtract(amount);
        bankAccount.setBalance(newBalance);
        bankAccountRepository.save(bankAccount);
    }

    public void increaseBalance(BigDecimal amount, BankAccount bankAccount) {
        BigDecimal newBalance = bankAccount.getBalance().add(amount);
        bankAccount.setBalance(newBalance);
        bankAccountRepository.save(bankAccount);
    }

    public ResponseEntity<?> getBankAccounts() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException(""));
        //System.out.println(user.getBankAccounts().size());
        List<BankAccount> bankAccounts =
                bankAccountRepository.findByUserAndSavingIsNullAndCreditIsNullOrderByOpeningDateDesc(user);
        //System.out.println(bankAccounts);
        System.out.println(bankAccounts.size());

        System.out.println(userEmail);
        System.out.println(LocalTime.now());

        return ResponseEntity.status(HttpStatus.OK).body(bankAccounts);
        //return ResponseHandler.generateResponse("Ok",HttpStatus.OK);
    }
}
