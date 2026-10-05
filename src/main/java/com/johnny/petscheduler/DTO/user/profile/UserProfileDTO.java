package com.johnny.petscheduler.DTO.user.profile;

import com.johnny.petscheduler.security.user.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

/**
 * This class holds base user information.
 */
@Getter @Setter
@NoArgsConstructor
public abstract class UserProfileDTO
{
    private Long id;
    private String firstName;
    private String lastName;
    private String avatarURL;
    private Instant lastActive;
    private UserRole role;
}
