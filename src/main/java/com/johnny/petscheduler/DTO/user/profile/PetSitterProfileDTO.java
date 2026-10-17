package com.johnny.petscheduler.DTO.user.profile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This represents a DTO containing profile information for a PetSitter.
 */
@Getter
@Setter
@NoArgsConstructor
public class PetSitterProfileDTO extends UserProfileDTO
{
    private String postalCode;
    private String profileTitle;
    private String profileDescription;
    private Double rating;
    private Integer yearsOfExperience;
}
