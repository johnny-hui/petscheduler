package com.johnny.petscheduler.DTO.authentication;

/**
 * A DTO encapsulating data for a new JWT access token.
 *
 * @implNote <code>Usage</code>
 *      This DTO is distributed when "/api/auth/refresh" endpoint is called
 *
 * @param access
 *      A String representing the JWT access token
 */
public record JWTNewAccessDTO(
        String access
) {}
