package com.tofi.bankingsystem.exceptions;

public class IncorrectVerificationCodeException extends Exception{
    public IncorrectVerificationCodeException(String message) {
        super(message);
    }
}
