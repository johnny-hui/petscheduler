package com.johnny.petscheduler.DTO.authentication;

/**
 * A record that contains data for a JWT access token when a User has
 * successfully been authenticated.
 *
 * @implNote <code>JWT Refresh</code>
 *      The refresh token is not included here because it is included in
 *      an HttpOnly cookie attached to a ResponseEntity.
 *
 * @see java.lang.Record
 *      Records are technically Java classes that are immutable
 *      with automatically generated getters ONLY. Best used
 *      to represent DTOs or database results.
 *
 * @param access
 *      A string containing the access token
 */
public record JWTLoginResponseDTO(
        String access
) {}
