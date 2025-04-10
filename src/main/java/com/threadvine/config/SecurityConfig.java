package com.threadvine.config;

import com.threadvine.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Slf4j
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
       log.info( "Configuring security filter chain" );
        return httpSecurity
                .csrf(csrfConfigurer -> csrfConfigurer.disable())
                .authorizeHttpRequests(requestsConfigurer -> requestsConfigurer
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers( HttpMethod.GET,"/api/products/**").permitAll()
                        .requestMatchers( "/api/v1/auth/change-password" ).authenticated()
                        .anyRequest().authenticated()
                 )
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        log.info( "Configuring authentication manager");
        return config.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationService authenticationService) {
        log.info( "Configuring JWT authentication filter" );
        return new JwtAuthenticationFilter(authenticationService);
    }


}
