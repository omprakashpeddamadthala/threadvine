package com.threadvine.service.impl;

import com.threadvine.model.User;
import com.threadvine.repositories.UserRepository;
import com.threadvine.service.AuthenticationService;
import com.threadvine.service.TokenBlackListService;
import com.threadvine.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final TokenBlackListService tokenBlackListService;
    private final JwtUtil jwtUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + username + " not found"));
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return jwtUtil.createToken(claims, userDetails.getUsername());
    }

    @Override
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && 
                !jwtUtil.isTokenExpired(token) && 
                !tokenBlackListService.isTokenIsBlockListed(token));
    }

    @Override
    public String extractUsername(String token) {
        return jwtUtil.extractUsername(token);
    }
}