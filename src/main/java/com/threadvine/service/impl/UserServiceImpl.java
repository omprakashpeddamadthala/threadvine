package com.threadvine.service.impl;

import com.threadvine.exceptions.UserNotFoundException;
import com.threadvine.io.ChangePasswordRequest;
import com.threadvine.model.User;
import com.threadvine.repositories.UserRepository;
import com.threadvine.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        log.info( "Registering user: {}", user.getEmail());
        if(userRepository.findByEmail( user.getEmail() ).isPresent()){
            throw new IllegalArgumentException("User already exists with email: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
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

}
