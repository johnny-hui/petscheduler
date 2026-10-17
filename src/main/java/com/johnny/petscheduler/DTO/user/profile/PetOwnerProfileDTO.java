package com.johnny.petscheduler.DTO.user.profile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This represents a DTO containing profile information for a PetOwner.
 */
@Getter
@Setter
@NoArgsConstructor
public class PetOwnerProfileDTO extends UserProfileDTO
{
    private String bio;
}
