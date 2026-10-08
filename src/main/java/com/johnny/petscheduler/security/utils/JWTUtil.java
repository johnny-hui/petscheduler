package com.johnny.petscheduler.security.utils;

import com.johnny.petscheduler.exceptions.InvalidJWTException;
import com.johnny.petscheduler.security.jwt.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

import static com.johnny.petscheduler.security.configs.SecurityConstants.*;

@Component
public class JWTUtil
{
    @Value("${jwt.access.expiration.time}")
    private int jwtAccessExpirationTimeMs;

    @Value("${jwt.refresh.expiration.time}")
    private int jwtRefreshExpirationTimeMs;

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    @Autowired
    public JWTUtil(PrivateKey privateKey, PublicKey publicKey)
    {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    /**
     * Returns a signed JWT token.
     *
     * @implNote JWT Payload Section
     * <code>
     *     {
     *      *   "sub": "johnny@example.com",
     *      *   "token_type": "refresh",
     *      *   "iat": 1791350000,
     *      *   "exp": 1793942000
     *      * }
     * </code>
     *
     * @param email
     *      A String for the user's email address
     *
     * @param tokenType
     *      The type of token (Access or Refresh)
     *
     * @return <code>token</code>
     *      A String containing the signed JWT token
     */
    public String generateToken(final String email,
                                final TokenType tokenType)
    {
        switch(tokenType)
        {
            case ACCESS -> {
                return Jwts.builder()
                        .subject(email)
                        .claim(TOKEN_TYPE_HEADER, JWT_ACCESS)
                        .issuedAt(new Date())
                        .expiration(new Date(System.currentTimeMillis() + jwtAccessExpirationTimeMs))
                        .signWith(privateKey, Jwts.SIG.ES256)
                        .compact();
            }
            case REFRESH -> {
                return Jwts.builder()
                        .subject(email)
                        .claim(TOKEN_TYPE_HEADER, JWT_REFRESH)
                        .issuedAt(new Date())
                        .expiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationTimeMs))
                        .signWith(privateKey, Jwts.SIG.ES256)
                        .compact();
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Parses JWT payload section for the subject which contains
     * the user's email.
     *
     * @param token
     *      A String representation of the JWT
     *
     * @return email
     *      A String for the user's email
     */
    public String getEmailFromToken(final String token)
    {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Validates the JWT token.
     *
     * @throws InvalidJWTException
     *      Thrown when JWT token is invalid
     *
     * @param token
     *      A String representation of the JWT
     *
     * @return T/F
     *      True if token valid, <Code>InvalidJWTException</Code> is thrown if false
     */
    public boolean validateToken(final String token) throws InvalidJWTException
    {
        try {
            Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (final Exception ex) {
            throw new InvalidJWTException(ex.getMessage(), ex);
        }
    }

    /**
     * Determines if a JWT token is a refresh token.
     *
     * @param token
     *      A String representing the JWT token
     *
     * @return <code>bool (T/F)</code>
     *      True if refresh; false otherwise
     */
    public boolean isTokenRefresh(final String token)
    {
        Claims claims = Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get(TOKEN_TYPE_HEADER).equals(JWT_REFRESH);
    }
}
