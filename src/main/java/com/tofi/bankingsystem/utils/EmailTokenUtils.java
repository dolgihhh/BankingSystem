package com.tofi.bankingsystem.utils;
import java.security.SecureRandom;
//import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class EmailTokenUtils {

    public String generateVerificationCode() {
        SecureRandom secureRandom = new SecureRandom();
        int sixDigitCode = secureRandom.nextInt(100000,1000000);

        return String.valueOf(sixDigitCode);
    }
}
