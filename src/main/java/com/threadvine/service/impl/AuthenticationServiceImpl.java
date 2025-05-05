package com.threadvine.service.impl;

import com.threadvine.model.User;
import com.threadvine.repositories.UserRepository;
import com.threadvine.service.AuthenticationService;
import com.threadvine.service.TokenBlackListService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private String expiration;

    private final UserRepository userRepository;

    private final TokenBlackListService tokenBlackListService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User with email " + username + " not found"));
        return user;
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        log.info( "Generating JWT token for user: {}", userDetails.getUsername() );
        Map<String, Object> claims = new HashMap<>();
        return this.createToken(claims,userDetails.getUsername());
    }

    @Override
    public Boolean validateToken(String token, UserDetails userDetails) {
        log.info( "Validating JWT token for user: {}", userDetails.getUsername() );
        final String username = this.extractUsername(token);
        return ( username.equals( userDetails.getUsername() ) && !this.isTokenExpired(token) &&  tokenBlackListService.isTokenIsBlockListed(token));
    }


    private <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
        log.info( "Extracting claim from JWT token" );
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        log.info( "Extracting all claims from JWT token" );
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    private String createToken(Map<String, Object> claims, String username) {
        log.info( "Creating JWT token for user: {}", username );
        return Jwts.builder()
                .setClaims( claims )
                .setSubject( username )
                .setIssuedAt( new Date( System.currentTimeMillis() ) )
                .setExpiration( new Date( System.currentTimeMillis() + 86400000L ) )
                .signWith( SignatureAlgorithm.HS256, secretKey )
                .compact();
    }

    private Boolean isTokenExpired(String token) {
        log.info( "Checking if JWT token is expired" );
        return this.extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        log.info( "Extracting expiration date from JWT token" );
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        log.info( "Extracting username from JWT token" );
        return this.extractClaim(token, Claims::getSubject);
    }


}
