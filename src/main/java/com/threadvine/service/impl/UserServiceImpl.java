package com.threadvine.service.impl;

import com.threadvine.exceptions.UserNotFoundException;
import com.threadvine.io.ChangePasswordRequest;
import com.threadvine.model.User;
import com.threadvine.records.RegisterRequest;
import com.threadvine.repositories.UserRepository;
import com.threadvine.service.EmailService;
import com.threadvine.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public User registerUser(RegisterRequest registerRequest) {
        log.info("Registering user: {}", registerRequest.email());
        if(userRepository.findByEmail(registerRequest.email()).isPresent()){
            throw new IllegalArgumentException("User already exists with email: " + registerRequest.email());
        }

        // Generate random password
        String temporaryPassword = generateRandomPassword();

        User user = User.builder()
                .email(registerRequest.email())
                .build();

        // Default to a valid role
        if (!isValidRole(registerRequest.role().name())){
            user.setRole(User.Role.USER);
        } else {
            user.setRole(registerRequest.role());
        }

        user.setPassword(passwordEncoder.encode(temporaryPassword));
        User savedUser = userRepository.save(user);

        // Send registration email asynchronously
        emailService.sendRegistrationEmail(savedUser, temporaryPassword);

        return savedUser;
    }


    public void changePassword(String email, ChangePasswordRequest changePasswordRequest) {
        log.info( "Changing password for user: {}", email );
        User user = this.getUserByEmail(email);
        if(!passwordEncoder.matches( changePasswordRequest.getCurrentPassword(), user.getPassword() )){
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        log.info( "Getting user by email: {}", email );
        return userRepository.findByEmail( email )
                .orElseThrow(()->new UserNotFoundException("User not found with email: " + email));
    }

    private boolean isValidRole(String role) {
        return Arrays.asList("USER", "ADMIN", "SELLER","BUYER", "SALES_REP").contains(role);
    }

    // Generate a random password
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }

        return sb.toString();
    }

}
