package com.johnny.petscheduler.security.filters;

import com.johnny.petscheduler.security.jwt.AuthEntryPointJWT;
import com.johnny.petscheduler.security.user.CustomUserDetailsService;
import com.johnny.petscheduler.security.utils.JWTUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

import static com.johnny.petscheduler.security.configs.SecurityConstants.*;

@Component
public class JWTAuthFilter extends OncePerRequestFilter
{
    private final JWTUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthEntryPointJWT authEntryPointJWT;

    @Autowired
    public JWTAuthFilter(final JWTUtil jwtUtil,
                         final CustomUserDetailsService customUserDetailsService,
                         final AuthEntryPointJWT authEntryPointJWT)
    {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
        this.authEntryPointJWT = authEntryPointJWT;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException
    {
        try {
            String token = parseRequestForJWT(request);
            if(token != null) {
                Claims claims = jwtUtil.parseToken(token);
                String email = jwtUtil.getEmailFromToken(claims);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                // Set Authentication Context (once JWT auth has passed)
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);
            }
        } catch (final Exception e) {
            authEntryPointJWT.commence(
                    request,
                    response,
                    new InsufficientAuthenticationException(UNAUTHORIZED_ERROR_MSG, e)
            );
            return;
        }
        // Pass on request and response to next filter in the chain
        filterChain.doFilter(request, response);
    }

    private String parseRequestForJWT(final HttpServletRequest request)
    {
        String headerAuth = request.getHeader(AUTH_HEADER_STRING);
        if (headerAuth != null && headerAuth.startsWith(TOKEN_PREFIX_BEARER)) {
            return headerAuth.substring(TOKEN_PREFIX_BEARER.length());
        }
        return null;
    }
}
