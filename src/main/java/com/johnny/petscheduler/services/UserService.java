package com.johnny.petscheduler.services;

import com.johnny.petscheduler.DAO.UserDAO;
import com.johnny.petscheduler.DTO.user.profile.UserProfileDTO;
import com.johnny.petscheduler.DTO.user.update.UpdatePetBusinessDTO;
import com.johnny.petscheduler.DTO.user.update.UpdatePetOwnerDTO;
import com.johnny.petscheduler.DTO.user.update.UpdatePetSitterDTO;
import com.johnny.petscheduler.entities.users.PetBusiness;
import com.johnny.petscheduler.entities.users.PetOwner;
import com.johnny.petscheduler.entities.users.PetSitter;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.exceptions.EmailAlreadyExistsException;
import com.johnny.petscheduler.exceptions.UserNotFoundException;
import com.johnny.petscheduler.utils.UserUpdateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.*;
import static com.johnny.petscheduler.utils.UserProfileUtil.createProfileDTO;

@Service
public class UserService {
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserDAO userDAO, PasswordEncoder passwordEncoder)
    {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserProfileDTO getCurrentUserProfile(final Authentication authentication)
    {
        User user = (User) authentication.getPrincipal();
        if (!userDAO.doesUserExist(user.getEmail())) {
            throw new UserNotFoundException(ERROR_MESSAGE_USER_NOT_FOUND);
        }
        return createProfileDTO(user);
    }

    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfile(final long id)
    {
        User user = userDAO.getUserById(id);
        if(user == null) {
            throw new UserNotFoundException(ERROR_MESSAGE_GENERAL_USER_NOT_FOUND);
        }
        return createProfileDTO(user);
    }

    @Transactional
    public ResponseEntity<Void> updatePetOwner(final UpdatePetOwnerDTO request,
                                               final Authentication authentication)
    {
        // Check if user wants to update their email and validate for duplicates in DB
        if(request.getEmail() != null && userDAO.doesUserExist(request.getEmail().trim().toLowerCase(Locale.ROOT))) {
            throw new EmailAlreadyExistsException("UPDATE ERROR: " + ERROR_MESSAGE_EMAIL_ALREADY_EXISTS);
        }

        // Downcast to PetOwner
        User user = (User) authentication.getPrincipal();
        if(user instanceof PetOwner petOwner) {
            UserUpdateUtil.updateBaseUserInfo(petOwner, passwordEncoder, request);
            UserUpdateUtil.updatePetOwnerInfo(petOwner, request);
        }
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> updatePetSitter(final UpdatePetSitterDTO request,
                                                final Authentication authentication)
    {
        if(request.getEmail() != null && userDAO.doesUserExist(request.getEmail().trim().toLowerCase(Locale.ROOT))) {
            throw new EmailAlreadyExistsException("UPDATE ERROR: " + ERROR_MESSAGE_EMAIL_ALREADY_EXISTS);
        }

        User user = (User) authentication.getPrincipal();
        if(user instanceof PetSitter petSitter) {
            UserUpdateUtil.updateBaseUserInfo(petSitter, passwordEncoder, request);
            UserUpdateUtil.updatePetSitterInfo(petSitter, request);
        }
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> updatePetBusiness(final UpdatePetBusinessDTO request,
                                                  final Authentication authentication)
    {
        if(request.getEmail() != null && userDAO.doesUserExist(request.getEmail().trim().toLowerCase(Locale.ROOT))) {
            throw new EmailAlreadyExistsException("UPDATE ERROR: " + ERROR_MESSAGE_EMAIL_ALREADY_EXISTS);
        }

        User user = (User) authentication.getPrincipal();
        if(user instanceof PetBusiness petBusiness) {
            UserUpdateUtil.updateBaseUserInfo(petBusiness, passwordEncoder, request);
            UserUpdateUtil.updatePetBusinessInfo(petBusiness, request);
        }
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> deleteUser(final Authentication authentication)
    {
        User user = (User) authentication.getPrincipal();
        userDAO.deleteUser(user);
        return ResponseEntity.ok().build();
    }
}
