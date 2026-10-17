package com.johnny.petscheduler.DTO.user.profile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This represents a DTO containing profile information for a PetBusiness.
 */
@Getter
@Setter
@NoArgsConstructor
public class PetBusinessProfileDTO extends UserProfileDTO
{
    private String businessName;
    private String address;
    private String city;
    private Double rating;
    private Integer establishmentYear;
    private String websiteURL;
    private String phoneNumber;
}
