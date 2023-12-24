package com.tofi.bankingsystem.services;

import com.tofi.bankingsystem.dto.requests.SavingDTO;
import com.tofi.bankingsystem.dto.responses.SavingResponseDTO;
import com.tofi.bankingsystem.entities.BankAccount;
import com.tofi.bankingsystem.entities.Saving;
import com.tofi.bankingsystem.entities.User;
import com.tofi.bankingsystem.repositiries.SavingRepository;
import com.tofi.bankingsystem.utils.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SavingService {

    private final UserService userService;
    private final BankAccountService bankAccountService;
    private final SavingRepository savingRepository;


    public ResponseEntity<?> enableSavings(SavingDTO savingDTO) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException(""));
        Saving saving = user.getSaving();

        if (saving == null) {
            return createSaving(savingDTO, user);
        }

        if (saving.isOn()) {
            return ResponseHandler.generateErrorResponse("Savings already enabled", HttpStatus.CONFLICT);
        }

        saving.setOn(true);
        saving.setRoundingValue(savingDTO.getRoundingValue());
        savingRepository.save(saving);

        return ResponseHandler.generateResponse("Savings successfully enabled", HttpStatus.OK);
    }

    public ResponseEntity<?> disableSavings() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException(""));
        Saving saving = user.getSaving();

        if (saving == null) {
            return ResponseHandler.generateErrorResponse("Savings not created",
                    HttpStatus.BAD_REQUEST);
        }

        if (!saving.isOn()) {
            return ResponseHandler.generateErrorResponse("Savings already disabled",
                    HttpStatus.CONFLICT);
        }

        saving.setOn(false);
        savingRepository.save(saving);

        return ResponseHandler.generateResponse("Savings successfully disabled", HttpStatus.OK);
    }

    public ResponseEntity<?> createSaving(SavingDTO savingDTO, User user) {
        BankAccount newBankAccount = bankAccountService.createBankAccountInDB();
        Saving newSaving = new Saving();
        newSaving.setRoundingValue(savingDTO.getRoundingValue());
        newSaving.setBankAccount(newBankAccount);
        newSaving.setUser(user);
        savingRepository.save(newSaving);

        return ResponseHandler.generateResponse("Savings successfully enabled", HttpStatus.OK);
    }

    public void increaseTotalAccumulated(BigDecimal savingAmount, Saving saving) {
        BigDecimal newTotalAccumulated = saving.getTotalAccumulated().add(savingAmount);
        saving.setTotalAccumulated(newTotalAccumulated);
        savingRepository.save(saving);
    }

    public ResponseEntity<?> getSavings() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException(""));
        Saving saving = user.getSaving();
        if (saving == null) {
            return ResponseHandler.generateResponse("No savings",
                    HttpStatus.OK);
        }

        SavingResponseDTO savingResponseDTO = new SavingResponseDTO();
        savingResponseDTO.setOn(saving.isOn());
        savingResponseDTO.setRoundingValue(saving.getRoundingValue());
        savingResponseDTO.setTotalAccumulated(saving.getTotalAccumulated());
        savingResponseDTO.setBankAccountNumber(saving.getBankAccount().getNumber());
        savingResponseDTO.setMessage("Success");
        savingResponseDTO.setBalance(saving.getBankAccount().getBalance());
        return ResponseEntity.status(HttpStatus.OK).body(savingResponseDTO);
    }
}
