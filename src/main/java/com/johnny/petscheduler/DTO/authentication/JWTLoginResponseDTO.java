package com.johnny.petscheduler.DTO.authentication;

/**
 * A record that contains data for both JWT access and refresh
 * tokens when a User has successfully been authenticated.
 *
 * @see java.lang.Record
 *      Records are technically Java classes that are immutable
 *      with automatically generated getters ONLY. Best used
 *      to represent DTOs or database results.
 *
 * @param access
 *      A string containing the access token
 *
 * @param refresh
 *      A string containing the refresh token
 */
public record JWTLoginResponseDTO(
        String access,
        String refresh
) {}
