package com.tofi.bankingsystem.controllers;

import com.tofi.bankingsystem.utils.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class MainController {

    @GetMapping("/private")
    public ResponseEntity<?> privateApi() {

        return ResponseHandler.generateResponse("Accessed private endpoint", HttpStatus.OK);
    }
}
