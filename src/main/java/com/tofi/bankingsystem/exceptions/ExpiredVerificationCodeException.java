package com.tofi.bankingsystem.exceptions;

public class ExpiredVerificationCodeException extends Exception{
    public ExpiredVerificationCodeException(String message) {
        super(message);
    }
}
