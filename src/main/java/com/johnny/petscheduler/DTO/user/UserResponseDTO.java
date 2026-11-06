package com.johnny.petscheduler.DTO.user;

import com.johnny.petscheduler.security.user.UserRole;

/**
 * A record containing response data upon successful registration or login.
 *
 * @see <code>java.lang.Record</code>
 *      Records are technically Java classes that are immutable
 *      with automatically generated getters ONLY. Best used
 *      to represent DTOs or database results.
 */
public record UserResponseDTO(
        String firstName,
        String lastName,
        String email,
        UserRole role,
        String country,
        String city,
        String timezone,
        String avatarURL
){}
