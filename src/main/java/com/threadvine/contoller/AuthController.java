package com.threadvine.contoller;


import com.threadvine.io.AuthResponse;
import com.threadvine.io.ChangePasswordRequest;
import com.threadvine.io.LoginRequest;
import com.threadvine.model.User;
import com.threadvine.service.AuthenticationService;
import com.threadvine.service.TokenBlackListService;
import com.threadvine.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final TokenBlackListService tokenBlackListService;

    @Operation(summary = "Authenticate a user", description = "Authenticates a user with email and password and returns a JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "Login credentials", required = true)
            @Valid @RequestBody LoginRequest loginRequest) {
        log.info( "Logging in user: {}", loginRequest.getEmail() );
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken( loginRequest.getEmail(), loginRequest.getPassword() ) );

        final UserDetails userDetails = userService.getUserByEmail( loginRequest.getEmail() );
        final String jwt = authenticationService.generateToken( userDetails );
        return ResponseEntity.ok( AuthResponse.builder().email( loginRequest.getEmail() )
                .jwtToken( jwt ).build() );
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully registered", 
                content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "400", description = "Invalid input or email already in use", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Parameter(description = "User registration details", required = true)
            @Valid @RequestBody User user) {
        log.info("Received registration request for user: {}", user.getEmail());
        User registeredUser = userService.registerUser( user );
        return ResponseEntity.ok("User registered successfully with email: " + registeredUser.getEmail() );
    }

    @Operation(summary = "Change user password", description = "Changes the password for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password successfully changed", 
                content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Parameter(description = "Password change details", required = true)
            @Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        log.info( "Received change password request for user with email: {}", email);
        userService.changePassword( email, request );
        return ResponseEntity.ok().body( "Password changed" );
    }


    @Operation(summary = "Logout user", description = "Invalidates the user's JWT token by adding it to a blacklist")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully logged out", 
                content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "400", description = "No valid token provided", 
                content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @Parameter(description = "Request with Authorization header containing JWT token", required = true)
            HttpServletRequest httpServletRequest){
        log.info( "Received logout request  ");
        String token = extractTokenFromRequest(httpServletRequest);
        if(StringUtils.hasText(token)) {
            tokenBlackListService.addTokenToBlackList(token);
            return ResponseEntity.ok("Logged out successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No valid token provided");
        }
    }

    @Operation(summary = "Refresh JWT token", description = "Generates a new JWT token using the current valid token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token successfully refreshed",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or expired token", content = @Content)
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(
            @Parameter(description = "Request with Authorization header containing JWT token", required = true)
            HttpServletRequest httpServletRequest) {
        log.info("Received refresh token request");
        String token = extractTokenFromRequest(httpServletRequest);

        if (StringUtils.isEmpty(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String username = authenticationService.extractUsername(token);
            UserDetails userDetails = userService.getUserByEmail(username);

            if (authenticationService.validateToken(token, userDetails)) {
                // Generate a new token
                final String newToken = authenticationService.generateToken(userDetails);

                // Blacklist the old token
                tokenBlackListService.addTokenToBlackList(token);

                return ResponseEntity.ok(AuthResponse.builder()
                        .email(username)
                        .jwtToken(newToken)
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            log.error("Error refreshing token", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
