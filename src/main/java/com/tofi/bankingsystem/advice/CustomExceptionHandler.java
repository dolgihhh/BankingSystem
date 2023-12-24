package com.tofi.bankingsystem.advice;

import com.tofi.bankingsystem.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleInvalidArgument(MethodArgumentNotValidException exception) {
        Map<String, String> map =  new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError ->
                map.put(fieldError.getField(), fieldError.getDefaultMessage()));

        return map;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public Map<String, String> handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("error", exception.getMessage());
        
        return map;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public Map<String, String> handleBadCredentialsException() {
        Map<String, String> map = new HashMap<>();
        map.put("error", "Wrong email or password");

        return map;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public Map<String, String> handleAccessDeniedException(AccessDeniedException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("error", exception.getMessage());

        return map;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IncorrectVerificationCodeException.class)
    public Map<String, String> IncorrectVerificationCodeException(IncorrectVerificationCodeException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("error", exception.getMessage());

        return map;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(SameSenderAndRecipientAccException.class)
    public Map<String, String> SameSenderAndRecipientAccException(SameSenderAndRecipientAccException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("error", exception.getMessage());

        return map;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SenderAccNotExistsException.class)
    public Map<String, String> SenderAccNotExistsException(SenderAccNotExistsException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("error", exception.getMessage());

        return map;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RecipientAccNotExistsException.class)
    public Map<String, String> RecipientAccNotExistsException(RecipientAccNotExistsException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("error", exception.getMessage());

        return map;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InsufficientFundsException.class)
    public Map<String, String> RecipientAccNotExistsException(InsufficientFundsException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("error", exception.getMessage());

        return map;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CreditNotExistsException.class)
    public Map<String, String> CreditNotExistsException(CreditNotExistsException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("error", exception.getMessage());

        return map;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ExpiredVerificationCodeException.class)
    public Map<String, String> ExpiredVerificationCodeException(ExpiredVerificationCodeException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("error", exception.getMessage());

        return map;
    }
}

