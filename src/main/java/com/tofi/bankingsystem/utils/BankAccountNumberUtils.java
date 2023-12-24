package com.tofi.bankingsystem.utils;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class BankAccountNumberUtils {

    private final String countryCode = "BY";
    private final String bankCode = "BDPB";
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateBankAccountNumber() {
        int firstPart = secureRandom.nextInt(1000000);
        String firstPartStr = String.format("%06d", firstPart);
        int secondPart = secureRandom.nextInt(999999) + 1;
        String secondPartStr = String.format("%06d", secondPart);
        String controlNumber = generateRandomControlNumber();

        return countryCode + controlNumber + bankCode + firstPartStr + secondPartStr;
    }

    private String generateRandomControlNumber() {
        int controlNumberValue = secureRandom.nextInt(99) + 1;

        return String.format("%02d", controlNumberValue);
    }
}

