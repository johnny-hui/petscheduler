package com.johnny.petscheduler.DTO.user;

/**
 * A record containing response data upon successful registration or login.
 *
 * @see java.lang.Record
 *      Records are technically Java classes that are immutable
 *      with automatically generated getters ONLY. Best used
 *      to represent DTOs or database results.
 *
 * @param firstName
 *      A String for the User's first name
 *
 * @param lastName
 *      A String for the User's last name
 *
 * @param email
 *      A String for the User's email
 */
public record UserResponseDTO(
        String firstName,
        String lastName,
        String email
){}
