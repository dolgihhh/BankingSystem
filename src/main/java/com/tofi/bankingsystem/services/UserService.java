package com.tofi.bankingsystem.services;

import com.tofi.bankingsystem.dto.requests.UserRegistrationDTO;
import com.tofi.bankingsystem.entities.User;
import com.tofi.bankingsystem.repositiries.UserRepository;
import com.tofi.bankingsystem.utils.ResponseHandler;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User with email '%s' not found", email)
        ));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getVerificationCode(),
                Collections.emptyList()
        );
    }

    public Optional<User> findByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public ResponseEntity<?> createUser(UserRegistrationDTO registrationDTO) {
        try {
            User newUser = new User();
            newUser.setEmail(registrationDTO.getEmail());
            newUser.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
            newUser.setName(registrationDTO.getName());
            newUser.setSurname(registrationDTO.getSurname());

            userRepository.save(newUser);

            return ResponseHandler.generateResponse("User created", HttpStatus.OK);
        } catch (Exception e) {

            return ResponseHandler.generateErrorResponse(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
