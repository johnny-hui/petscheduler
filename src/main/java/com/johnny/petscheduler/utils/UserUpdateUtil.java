package com.johnny.petscheduler.utils;

import com.johnny.petscheduler.DTO.user.UpdatePetBusinessDTO;
import com.johnny.petscheduler.DTO.user.UpdatePetOwnerDTO;
import com.johnny.petscheduler.DTO.user.UpdatePetSitterDTO;
import com.johnny.petscheduler.DTO.user.UpdateUserDTO;
import com.johnny.petscheduler.entities.PetBusiness;
import com.johnny.petscheduler.entities.PetOwner;
import com.johnny.petscheduler.entities.PetSitter;
import com.johnny.petscheduler.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;

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
            user.setEmail(request.getEmail());
        }
        if(request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if(request.getPostalCode() != null) {
            user.setPostalCode(request.getPostalCode());
        }
        if(request.getAvatarURL() != null) {
            user.setAvatarURL(request.getAvatarURL());
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
            user.setBio(request.getBio());
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
            user.setProfileTitle(request.getProfileTitle());
        }
        if(request.getProfileDescription() != null) {
            user.setProfileDescription(request.getProfileDescription());
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
            user.setAddress(request.getAddress());
        }
        if(request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if(request.getProfileTitle() != null) {
            user.setProfileTitle(request.getProfileTitle());
        }
        if(request.getProfileDescription() != null) {
            user.setProfileDescription(request.getProfileDescription());
        }
        if(request.getWebsiteURL() != null) {
            user.setWebsiteURL(request.getWebsiteURL());
        }
        if(request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
    }
}
