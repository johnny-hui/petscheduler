package com.johnny.petscheduler.utils;

import com.johnny.petscheduler.DTO.user.profile.PetBusinessProfileDTO;
import com.johnny.petscheduler.DTO.user.profile.PetOwnerProfileDTO;
import com.johnny.petscheduler.DTO.user.profile.PetSitterProfileDTO;
import com.johnny.petscheduler.DTO.user.profile.UserProfileDTO;
import com.johnny.petscheduler.entities.users.PetBusiness;
import com.johnny.petscheduler.entities.users.PetOwner;
import com.johnny.petscheduler.entities.users.PetSitter;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.exceptions.UserProfileCannotBeLoadedException;
import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.ERROR_MESSAGE_USER_PROFILE_ERROR;

/**
 * This utility class provides functions that facilitates the creation
 * of UserProfileDTOs into their proper subtypes.
 */
public final class UserProfileUtil
{
    private UserProfileUtil() {}

    /**
     * Creates the appropriate UserProfileDTO according to the user's role.
     *
     * @param user
     *      A subtype of a User object
     *
     * @return UserProfileDTO
     *      A subtype of UserProfileDTO (Owner, Sitter, Business)
     */
    public static UserProfileDTO createProfileDTO(final User user)
    {
        switch(user.getRole()) {
            case PET_OWNER -> {
                PetOwner owner = (PetOwner) user;  // down-casting
                PetOwnerProfileDTO response = new PetOwnerProfileDTO();
                setBaseUserInformation(response, owner);
                setOwnerInformation(response, owner);
                return response;
            }
            case PET_SITTER -> {
                PetSitter sitter = (PetSitter) user;
                PetSitterProfileDTO response = new PetSitterProfileDTO();
                setBaseUserInformation(response, sitter);
                setSitterInformation(response, sitter);
                return new PetSitterProfileDTO();
            }
            case PET_BUSINESS -> {
                PetBusiness business = (PetBusiness) user;
                PetBusinessProfileDTO response = new PetBusinessProfileDTO();
                setBaseUserInformation(response, business);
                setBusinessInformation(response, business);
                return new PetBusinessProfileDTO();
            }
            default -> {
                throw new UserProfileCannotBeLoadedException(ERROR_MESSAGE_USER_PROFILE_ERROR);
            }
        }
    }

    private static void setBaseUserInformation(UserProfileDTO response,
                                               final User owner)
    {
        response.setId(owner.getId());
        response.setFirstName(owner.getFirstName());
        response.setLastName(owner.getLastName());
        response.setAvatarURL(owner.getAvatarURL());
        response.setLastActive(owner.getLastActive());
        response.setRole(owner.getRole());
    }

    private static void setOwnerInformation(PetOwnerProfileDTO response,
                                            final PetOwner owner)
    {
        response.setBio(owner.getBio());
    }

    private static void setSitterInformation(PetSitterProfileDTO response,
                                             final PetSitter sitter)
    {
        response.setPostalCode(sitter.getPostalCode());
        response.setProfileTitle(sitter.getProfileTitle());
        response.setProfileDescription(sitter.getProfileDescription());
        response.setYearsOfExperience(sitter.getYearsOfExperience());
    }

    private static void setBusinessInformation(PetBusinessProfileDTO response,
                                               final PetBusiness business)
    {
        response.setBusinessName(business.getBusinessName());
        response.setAddress(business.getAddress());
        response.setCity(business.getCity());
        response.setEstablishmentYear(business.getEstablishmentYear());
        response.setWebsiteURL(business.getWebsiteURL());
        response.setPhoneNumber(business.getPhoneNumber());
    }
}
