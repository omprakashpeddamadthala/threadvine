package com.threadvine.contoller;


import com.threadvine.io.AuthResponse;
import com.threadvine.io.ChangePasswordRequest;
import com.threadvine.io.LoginRequest;
import com.threadvine.model.User;
import com.threadvine.service.AuthenticationService;
import com.threadvine.service.TokenBlackListService;
import com.threadvine.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final TokenBlackListService tokenBlackListService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info( "Logging in user: {}", loginRequest.getEmail() );
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken( loginRequest.getEmail(), loginRequest.getPassword() ) );

        final UserDetails userDetails = userService.getUserByEmail( loginRequest.getEmail() );
        final String jwt = authenticationService.generateToken( userDetails );
        return ResponseEntity.ok( AuthResponse.builder().email( loginRequest.getEmail() )
                .jwtToken( jwt ).build() );
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody User user) {
        log.info("Received registration request for user: {}", user.getEmail());
        User registeredUser = userService.registerUser( user );
        return ResponseEntity.ok("User registered successfully with email: " + registeredUser.getEmail() );
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        log.info( "Received change password request for user with email: {}", email);
        userService.changePassword( email, request );
        return ResponseEntity.ok().body( "Password changed" );
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/logout")
    public void logout(HttpServletRequest httpServletRequest){
        log.info( "Received logout request  ");
        String token = extractTokenFromRequest(httpServletRequest);
        if(!StringUtils.isEmpty( token )) {
            tokenBlackListService.addTokenToBlackList( token );
        }
    }

    private String extractTokenFromRequest(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader( "Authorization");
        if(bearerToken!=null && bearerToken.startsWith( "Bearer " )) {
            return bearerToken.substring( 7 );
        }
        return null;
    }
}
