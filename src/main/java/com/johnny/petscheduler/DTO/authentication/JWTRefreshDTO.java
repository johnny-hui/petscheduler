package com.johnny.petscheduler.DTO.authentication;

import jakarta.validation.constraints.NotBlank;

/**
 * A DTO containing data for a JWT refresh token.
 *
 * @param refresh
 *      A String for the JWT refresh token
 */
public record JWTRefreshDTO(
        @NotBlank(message = "A refresh token must be provided!")
        String refresh
) {}
