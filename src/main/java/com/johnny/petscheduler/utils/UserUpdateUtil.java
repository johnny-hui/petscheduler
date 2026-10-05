package com.johnny.petscheduler.utils;

import com.johnny.petscheduler.DTO.user.update.UpdatePetBusinessDTO;
import com.johnny.petscheduler.DTO.user.update.UpdatePetOwnerDTO;
import com.johnny.petscheduler.DTO.user.update.UpdatePetSitterDTO;
import com.johnny.petscheduler.DTO.user.update.UpdateUserDTO;
import com.johnny.petscheduler.entities.users.PetBusiness;
import com.johnny.petscheduler.entities.users.PetOwner;
import com.johnny.petscheduler.entities.users.PetSitter;
import com.johnny.petscheduler.entities.users.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

/**
 * Contains utility functions to facilitate updating Users.
 */
public class UserUpdateUtil
{
    // Prevent Instantiation
    public UserUpdateUtil() {}

    /**
     * Updates base user information.
     *
     * @param user
     *      Any sub-class of User (PetOwner, PetSitter, PetBusiness)
     *
     * @param request
     *      Any sub-class of UpdateUserDTO that contains base info for updating
     */
    public static void updateBaseUserInfo(User user,
                                          final PasswordEncoder passwordEncoder,
                                          final UpdateUserDTO request)
    {
        if(request.getEmail() != null) {
            user.setEmail(request.getEmail().trim().toLowerCase(Locale.ROOT));
        }
        if(request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if(request.getPostalCode() != null) {
            user.setPostalCode(request.getPostalCode().trim().toUpperCase(Locale.ROOT));
        }
        if(request.getAvatarURL() != null) {
            user.setAvatarURL(request.getAvatarURL().trim());
        }
    }

    /**
     * Updates PetOwner information.
     *
     * @param user
     *      A PetOwner object
     *
     * @param request
     *      An UpdatePetOwnerDTO object
     */
    public static void updatePetOwnerInfo(PetOwner user, final UpdatePetOwnerDTO request)
    {
        if(request.getBio() != null) {
            user.setBio(request.getBio().trim());
        }
    }

    /**
     * Updates PetSitter information.
     *
     * @param user
     *      A PetSitter object
     *
     * @param request
     *      An UpdatePetSitterDTO object
     */
    public static void updatePetSitterInfo(PetSitter user, final UpdatePetSitterDTO request)
    {
        if(request.getProfileTitle() != null) {
            user.setProfileTitle(request.getProfileTitle().trim());
        }
        if(request.getProfileDescription() != null) {
            user.setProfileDescription(request.getProfileDescription().trim());
        }
    }

    /**
     * Updates PetBusiness information.
     *
     * @param user
     *      A PetBusiness object
     *
     * @param request
     *      An UpdatePetBusinessDTO object
     */
    public static void updatePetBusinessInfo(PetBusiness user, final UpdatePetBusinessDTO request)
    {
        if(request.getAddress() != null) {
            user.setAddress(request.getAddress().trim());
        }
        if(request.getBusinessType() != null) {
            user.setBusinessType(request.getBusinessType());
        }
        if(request.getCity() != null) {
            user.setCity(request.getCity().trim());
        }
        if(request.getProfileTitle() != null) {
            user.setProfileTitle(request.getProfileTitle().trim());
        }
        if(request.getProfileDescription() != null) {
            user.setProfileDescription(request.getProfileDescription().trim());
        }
        if(request.getWebsiteURL() != null) {
            user.setWebsiteURL(request.getWebsiteURL().trim());
        }
        if(request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber().trim());
        }
    }
}
