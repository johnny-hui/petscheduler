package com.johnny.petscheduler.security.configs;

import com.johnny.petscheduler.security.filters.JWTAuthFilter;
import com.johnny.petscheduler.security.jwt.AuthEntryPointJWT;
import com.johnny.petscheduler.security.user.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * A configuration class that loads beans required for authentication and authorization,
 * password hashing, and other security-related operations.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig
{
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthEntryPointJWT unauthorizedHandler;
    private final JWTAuthFilter jwtAuthFilter;

    @Autowired
    public SecurityConfig(final CustomUserDetailsService customUserDetailsService,
                          final AuthEntryPointJWT unauthorizedHandler,
                          final JWTAuthFilter jwtAuthFilter)
    {
        this.customUserDetailsService = customUserDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(customUserDetailsService);
        authProvider.setPasswordEncoder(this.passwordEncoder());
        return authProvider;
    }

    @Bean  // => Delegates authorization to DAOAuthenticationProvider
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http
                // Disable CSRF for a stateless REST API
                .csrf(AbstractHttpConfigurer::disable)

                // Set to authorization to stateless
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Set custom unauthorized exception handler
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(unauthorizedHandler)
                )

                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public APIs (no auth required) -> ex: Login or Register
                        .requestMatchers("/api/auth/**").permitAll()

                        // Add any protected APIs here
                        .anyRequest().authenticated()
                )

                // Add any custom filters here
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
