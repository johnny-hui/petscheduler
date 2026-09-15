package com.johnny.petscheduler.DTO.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * A DTO that binds login information for a User during the login process.
 *
 * @see java.lang.Record
 *      Records are technically Java classes that are immutable
 *      with automatically generated getters ONLY. Best used
 *      to represent DTOs or database results.
 *
 * @param email
 *      A String representing the email field
 *
 * @param password
 *      A String representing the password field
 */
public record LoginUserDTO(
        @Email(message = "A valid email must be provided")
        @NotBlank(message = "A valid email must be provided")
        String email,

        @NotBlank(message = "A password must be provided")
        String password
) {}
