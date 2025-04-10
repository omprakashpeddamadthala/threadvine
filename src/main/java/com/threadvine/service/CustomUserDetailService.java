package com.threadvine.service;

import com.threadvine.model.CustomUserDetail;
import com.threadvine.model.User;
import com.threadvine.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
          User user = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User with email " + username + " not found"));
        return new CustomUserDetail( user );
    }
}
