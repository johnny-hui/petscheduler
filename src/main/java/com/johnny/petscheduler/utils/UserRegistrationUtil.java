package com.johnny.petscheduler.utils;

import com.johnny.petscheduler.DTO.registration.RegisterPetBusinessDTO;
import com.johnny.petscheduler.DTO.registration.RegisterPetOwnerDTO;
import com.johnny.petscheduler.DTO.registration.RegisterPetSitterDTO;
import com.johnny.petscheduler.DTO.registration.RegisterUserDTO;
import com.johnny.petscheduler.entities.PetBusiness;
import com.johnny.petscheduler.entities.PetOwner;
import com.johnny.petscheduler.entities.PetSitter;
import com.johnny.petscheduler.entities.User;
import com.johnny.petscheduler.security.user.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

/**
 * A utility class that provides methods to facilitate User registration.
 */
public final class UserRegistrationUtil
{
    // Prevent object instantiation
    private UserRegistrationUtil() {}

    /**
     * Sets base information for a User object from info provided by
     * request DTO object.
     *
     * @param user
     *      Any User sub-class (PetOwner, PetSitter, PetBusiness)
     *
     * @param role
     *      A value from UserRole
     *
     * @param request
     *      Any sub-class of RegisterUserDTO that contains base info for registration
     */
    public static void setBaseUserInfo(User user,
                                       final PasswordEncoder passwordEncoder,
                                       final UserRole role,
                                       final RegisterUserDTO request)
    {
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase(Locale.ROOT));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPostalCode(request.getPostalCode().trim().toUpperCase(Locale.ROOT));
        user.setAvatarURL(request.getAvatarURL() != null ? request.getAvatarURL().trim() : null);
        user.setRole(role);
    }

    /**
     * Sets PetOwner related information.
     *
     * @param user
     *      A reference to PetOwner object
     *
     * @param request
     *      A reference to the PetOwnerDTO object
     */
     public static void setPetOwnerInfo(PetOwner user, final RegisterPetOwnerDTO request)
     {
         user.setBio(request.getBio());
     }

    /**
     * Sets PetSitter related information.
     *
     * @param user
     *      A reference to PetSitter object
     *
     * @param request
     *      A reference to the PetSitterDTO object
     */
     public static void setPetSitterInfo(PetSitter user, final RegisterPetSitterDTO request)
     {
         user.setProfileTitle(request.getProfileTitle());
         user.setProfileDescription(request.getProfileDescription());
         user.setYearsOfExperience(request.getYearsOfExperience());
     }

    /**
     * Sets PetBusiness related information.
     *
     * @param user
     *      A reference to PetBusiness object
     *
     * @param request
     *      A reference to the PetBusinessDTO object
     */
     public static void setPetBusinessInfo(PetBusiness user, final RegisterPetBusinessDTO request)
     {
        user.setBusinessName(request.getBusinessName().trim());
        user.setAddress(request.getAddress().trim());
        user.setCity(request.getCity().trim());
        user.setEstablishmentYear(request.getEstablishmentYear());
        user.setProfileTitle(request.getProfileTitle());
        user.setProfileDescription(request.getProfileDescription());
        user.setWebsiteURL(request.getWebsiteURL() != null ? request.getWebsiteURL().trim() : null);
        user.setPhoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber().trim() : null);
     }
}
