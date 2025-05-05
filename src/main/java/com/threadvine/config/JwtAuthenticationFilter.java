package com.threadvine.config;

import com.threadvine.service.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info( "Processing JWT authentication filter" );
        if (shouldSkipFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = this.extractToken(request);
        String username = token != null ? authenticationService.extractUsername(token) : null;
        if (username != null) {
            UserDetails userDetails = this.authenticationService.loadUserByUsername(username);
            if (token != null && authenticationService.validateToken(token, userDetails)) {
                log.info( "JWT token is valid" );
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean shouldSkipFilter(HttpServletRequest request) {
        log.info( "Checking if JWT authentication filter should be skipped");
        String path = request.getServletPath();
        return path.endsWith("/login") || 
               path.endsWith("/register") || 
               path.contains("/swagger-ui/") || 
               path.contains("/v3/api-docs/");
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader( "Authorization" );
        if (bearerToken != null && bearerToken.startsWith( "Bearer " )) {
            return bearerToken.substring( 7 );
        }
        return null;
    }
}
