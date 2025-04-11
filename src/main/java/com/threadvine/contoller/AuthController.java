package com.threadvine.contoller;


import com.threadvine.io.AuthResponse;
import com.threadvine.io.ChangePasswordRequest;
import com.threadvine.io.LoginRequest;
import com.threadvine.model.User;
import com.threadvine.service.AuthenticationService;
import com.threadvine.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        log.info( "Logging in user: {}", loginRequest.getEmail() );
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken( loginRequest.getEmail(), loginRequest.getPassword() ) );

        final UserDetails userDetails = userService.getUserByEmail( loginRequest.getEmail() );
        final String jwt = authenticationService.generateToken( userDetails );
        return ResponseEntity.ok( AuthResponse.builder().email( loginRequest.getEmail() )
                .jwtToken( jwt ).build() );
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        log.info("Received registration request for user: {}", user.getEmail());
        return ResponseEntity.ok( userService.registerUser( user ) );
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        log.info( "Received change password request for user with email: {}", email);
        userService.changePassword( email, request );
        return ResponseEntity.ok().body( "Password changed" );
    }
}
