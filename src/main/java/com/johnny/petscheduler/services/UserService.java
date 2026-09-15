package com.johnny.petscheduler.services;

import com.johnny.petscheduler.DAO.UserDAO;
import com.johnny.petscheduler.DTO.user.UpdatePetBusinessDTO;
import com.johnny.petscheduler.DTO.user.UpdatePetOwnerDTO;
import com.johnny.petscheduler.DTO.user.UpdatePetSitterDTO;
import com.johnny.petscheduler.DTO.user.UserResponseDTO;
import com.johnny.petscheduler.entities.users.PetBusiness;
import com.johnny.petscheduler.entities.users.PetOwner;
import com.johnny.petscheduler.entities.users.PetSitter;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.exceptions.EmailAlreadyExistsException;
import com.johnny.petscheduler.exceptions.UserNotFoundException;
import com.johnny.petscheduler.utils.UserUpdateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.ERROR_MESSAGE_EMAIL_ALREADY_EXISTS;
import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.ERROR_MESSAGE_USER_NOT_FOUND;

@Service
public class UserService {
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(final String email) {
        if (!userDAO.doesUserExist(email)) {
            throw new UserNotFoundException(ERROR_MESSAGE_USER_NOT_FOUND);
        }
        User user = userDAO.getUserByEmail(email);
        return new UserResponseDTO(user.getFirstName(), user.getLastName(), user.getEmail());
    }

    @Transactional
    public ResponseEntity<Void> updatePetOwner(final Long id, final UpdatePetOwnerDTO request) {
        if(request.getEmail() != null && userDAO.doesUserExist(request.getEmail().trim().toLowerCase(Locale.ROOT))) {
            throw new EmailAlreadyExistsException(ERROR_MESSAGE_EMAIL_ALREADY_EXISTS);
        }

        User user = userDAO.getUserById(id);
        if(user instanceof PetOwner petOwner) {
            UserUpdateUtil.updateBaseUserInfo(petOwner, passwordEncoder, request);
            UserUpdateUtil.updatePetOwnerInfo(petOwner, request);
        }
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> updatePetSitter(final Long id, final UpdatePetSitterDTO request) {
        if(request.getEmail() != null && userDAO.doesUserExist(request.getEmail().trim().toLowerCase(Locale.ROOT))) {
            throw new EmailAlreadyExistsException(ERROR_MESSAGE_EMAIL_ALREADY_EXISTS);
        }

        User user = userDAO.getUserById(id);
        if(user instanceof PetSitter petSitter) {
            UserUpdateUtil.updateBaseUserInfo(petSitter, passwordEncoder, request);
            UserUpdateUtil.updatePetSitterInfo(petSitter, request);
        }

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> updatePetBusiness(final Long id, final UpdatePetBusinessDTO request) {
        if(request.getEmail() != null && userDAO.doesUserExist(request.getEmail().trim().toLowerCase(Locale.ROOT))) {
            throw new EmailAlreadyExistsException(ERROR_MESSAGE_EMAIL_ALREADY_EXISTS);
        }

        User user = userDAO.getUserById(id);
        if(user instanceof PetBusiness petBusiness) {
            UserUpdateUtil.updateBaseUserInfo(petBusiness, passwordEncoder, request);
            UserUpdateUtil.updatePetBusinessInfo(petBusiness, request);
        }

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> deleteUser(final Long id) {
        if(!userDAO.doesUserExist(id)) {
            throw new UserNotFoundException(ERROR_MESSAGE_USER_NOT_FOUND);
        }

        User user = userDAO.getUserById(id);
        userDAO.deleteUser(user);
        return ResponseEntity.ok().build();
    }
}
