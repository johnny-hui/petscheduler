package com.johnny.petscheduler.security.configs;

import com.johnny.petscheduler.security.jwt.AuthEntryPointJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * A configuration class that loads beans required for authentication and authorization,
 * password hashing, and other security-related operations.
 */
@Configuration
public class SecurityConfig
{
    private final AuthEntryPointJWT unauthorizedHandler;

    @Autowired
    public SecurityConfig(AuthEntryPointJWT unauthorizedHandler) {
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for a stateless REST API
                .csrf(AbstractHttpConfigurer::disable)

                // Custom un-authorization handler
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler)
                )

                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public APIs (no auth required) -> ex: Login or Register
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()

                )

                // TODO: Remove basic auth after JWT
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
