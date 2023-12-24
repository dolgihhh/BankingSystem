package com.tofi.bankingsystem.services;

import com.tofi.bankingsystem.dto.requests.UserFirstLoginDTO;
import com.tofi.bankingsystem.dto.requests.UserRegistrationDTO;
import com.tofi.bankingsystem.dto.requests.UserSecondLoginDTO;
import com.tofi.bankingsystem.dto.responses.LoginResponseDTO;
import com.tofi.bankingsystem.entities.User;
import com.tofi.bankingsystem.exceptions.ExpiredVerificationCodeException;
import com.tofi.bankingsystem.exceptions.IncorrectVerificationCodeException;
import com.tofi.bankingsystem.exceptions.UserAlreadyExistsException;
import com.tofi.bankingsystem.utils.EmailTokenUtils;
import com.tofi.bankingsystem.utils.JwtTokenUtils;
import com.tofi.bankingsystem.utils.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final EmailTokenUtils emailTokenUtils;
    private final EmailService emailService;

    @Transactional
    public ResponseEntity<?> registerUser(UserRegistrationDTO registrationDTO) throws UserAlreadyExistsException {
        if (userService.userExistsByEmail(registrationDTO.getEmail())) {
            throw new UserAlreadyExistsException("User with given email already exists"); // goes to CustomExceptionHandler
        }

        return userService.createUser(registrationDTO);
    }


    @Transactional
    public ResponseEntity<?> firstAuthenticateUser(UserFirstLoginDTO firstLoginDTO) {
        User user =
                userService.findByEmail(firstLoginDTO.getEmail()).orElseThrow(() -> new UsernameNotFoundException("Wrong email"));

        if(!passwordEncoder.matches(firstLoginDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Wrong password");
        }

        String verificationCode = emailTokenUtils.generateVerificationCode();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Verification code from Bank System");
        mailMessage.setText("Your verification code : " + verificationCode);
        emailService.sendEmail(mailMessage);

        user.setVerificationCode(passwordEncoder.encode(verificationCode));
        user.setVerificationCodeExpireTime(LocalDateTime.now().plusMinutes(240));
        userService.updateUser(user);

        return ResponseHandler.generateResponse("Correct email and password. Verification" +
                        " code has been sent to your email", HttpStatus.OK);
    }


    public ResponseEntity<?> secondAuthenticateUser(UserSecondLoginDTO secondLoginDTO) throws
            IncorrectVerificationCodeException, ExpiredVerificationCodeException {
        String email = secondLoginDTO.getEmail();
        User user = userService.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User with email '%s' not found", email)
        ));

        if(user.getVerificationCodeExpireTime().isBefore(LocalDateTime.now())) {
            throw new ExpiredVerificationCodeException("Verification code expired. Login again");
        }

        if (!passwordEncoder.matches(secondLoginDTO.getVerificationCode(),
                user.getVerificationCode())) {
            throw new IncorrectVerificationCodeException("Incorrect verification code");
        }

        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(secondLoginDTO.getEmail(),
                        secondLoginDTO.getVerificationCode()));
        //calls userService.loadUserByUsername(loginDTO.getEmail());

        //SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenUtils.generateToken(user);

        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDTO(token));
    }

}