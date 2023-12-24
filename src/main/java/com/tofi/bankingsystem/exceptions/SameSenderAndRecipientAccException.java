package com.tofi.bankingsystem.exceptions;

public class SameSenderAndRecipientAccException extends Exception {
    public SameSenderAndRecipientAccException(String message) {
        super(message);
    }
}
